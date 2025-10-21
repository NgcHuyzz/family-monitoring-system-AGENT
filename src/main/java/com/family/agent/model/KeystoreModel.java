package com.family.agent.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeystoreModel {
	private String text;
	
	public static class KeyEvent 
	{
		private int charIndex;
		private long ts;
		
		public KeyEvent(int charIndex, long ts)
		{
			this.charIndex = charIndex;
			this.ts = ts;
		}
		
		public int getCharIndex()
		{
			return this.charIndex;
		}
		
		public long getTs()
		{
			return this.ts;
		}
	}
	
	private List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	
	public KeystoreModel()
	{
		
	}
	
	public KeystoreModel(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void addKeyEvent(int charIndex, long ts)
	{
		keyEvents.add(new KeyEvent(charIndex, ts));
	}
	
	public void removeLastKeyEvent()
	{
		if (!keyEvents.isEmpty()) 
			keyEvents.remove(keyEvents.size()-1);
	}
	
	public List<KeyEvent> getKeyEvents()
	{
		return Collections.unmodifiableList(keyEvents);
	}
	
	public void resetKeyEvent()
	{
		 keyEvents.clear();
	}
}

