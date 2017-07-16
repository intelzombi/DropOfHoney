package com.gunsnhony.dropofhoney.support;

import java.lang.reflect.Field;
import android.content.Context;

public class RProxy
{
	public boolean DEBUG;
	
	private String packageName;
	
	public RProxy(final Context context) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		packageName = context.getPackageName();
		
		final Class<?> BuildConfig = Class.forName(packageName + ".BuildConfig");
		final Field field = BuildConfig.getField("DEBUG");
		DEBUG = field.getBoolean(BuildConfig);
	}

	public int get(final String type, final String name) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		final Class<?> R = Class.forName(packageName + ".R$" + type);
		final Field field = R.getField(name);
		return field.getInt(R);
	}
	
	public int[] getArray(final String type, final String name) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		final Class<?> R = Class.forName(packageName + ".R$" + type);
		final Field field = R.getField(name);
		return (int[])field.get(R);
	}	
}
