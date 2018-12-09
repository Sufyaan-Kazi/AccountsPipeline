package com.suf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Desc2CategoryMap {
  public static Desc2CategoryMap INSTANCE = new Desc2CategoryMap();
  private ConcurrentMap<String, String> desc2CategoryMap = new ConcurrentHashMap<String, String>();

  private Desc2CategoryMap() {
    super();
    initialiseMapIfNecessary();
  }

  private synchronized void initialiseMapIfNecessary() {
    if (desc2CategoryMap.size() > 0) {
      return;
    }

    desc2CategoryMap.put("tfl ", "TRAVEL");
  }

  public String getCategoryForDesc(String desc) {
    String retVal = null;

    for (String key : desc2CategoryMap.keySet()) {
      // System.out.println("Checking key: " + key);
      if (desc.toLowerCase().indexOf(key) > -1) {
        // System.out.println("Found match: " + desc + " -> " + key);
        return desc2CategoryMap.get(desc);
      }
    }

    return retVal;
  }
}