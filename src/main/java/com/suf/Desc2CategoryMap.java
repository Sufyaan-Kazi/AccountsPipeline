package com.suf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Desc2CategoryMap {
  public static final Desc2CategoryMap INSTANCE = new Desc2CategoryMap();
  private ConcurrentMap<String, String> desc2CategoryMap = new ConcurrentHashMap<String, String>();

  private Desc2CategoryMap() {
    super();
    initialiseMapIfNecessary();
  }

  private synchronized void initialiseMapIfNecessary() {
    if (desc2CategoryMap.size() > 0) {
      return;
    }

    desc2CategoryMap.put("tfl ", Categories.TRAVEL);
    desc2CategoryMap.put("tesco store ", Categories.GROCERIES);
    desc2CategoryMap.put("waitrose ", Categories.GROCERIES);
    desc2CategoryMap.put("taste of lahore ", Categories.EATING_OUT);
    desc2CategoryMap.put("amazon ", Categories.SHOPPING);
    desc2CategoryMap.put("asda ", Categories.GROCERIES);
    desc2CategoryMap.put("apcoa-hal ", Categories.PARKING);
    desc2CategoryMap.put("apcoa ", Categories.PARKING);
    desc2CategoryMap.put("park ", Categories.PARKING);
    desc2CategoryMap.put("pret a manger ", Categories.EATING_OUT);
    desc2CategoryMap.put("interest  ", Categories.INTEREST);
    desc2CategoryMap.put("dixy chicken ", Categories.EATING_OUT);
    desc2CategoryMap.put("q park ", Categories.PARKING);
    desc2CategoryMap.put("transfer", Categories.TRANSFER);
    desc2CategoryMap.put("starbucks ", Categories.EATING_OUT);
    desc2CategoryMap.put("donate ", Categories.DONATIONS);
    desc2CategoryMap.put("wh smith ", Categories.SHOPPING);
    desc2CategoryMap.put("sainsburys ", Categories.GROCERIES);
    desc2CategoryMap.put("halfords ", Categories.CAR);
    desc2CategoryMap.put("google play ", Categories.SHOPPING);
    desc2CategoryMap.put("istanbul express ", Categories.GROCERIES);
    desc2CategoryMap.put("vue bsl ", Categories.ENTERTAINMENT);
    desc2CategoryMap.put("vodafone ", Categories.PHONE);
    desc2CategoryMap.put("wenzelstannes", Categories.GROCERIES);
    desc2CategoryMap.put("home ", Categories.HOME_IMPROV);
    desc2CategoryMap.put("marks spencer ", Categories.SHOPPING);
    desc2CategoryMap.put("new look ", Categories.SHOPPING);
    desc2CategoryMap.put("primark ", Categories.SHOPPING);
    desc2CategoryMap.put("superdrug ", Categories.BEAUTY);
    desc2CategoryMap.put("boots ", Categories.BEAUTY);
    desc2CategoryMap.put("espares ", Categories.DIY);
    desc2CategoryMap.put("steakout ", Categories.EATING_OUT);
    desc2CategoryMap.put("service stati ", Categories.FUEL);
    desc2CategoryMap.put("subway ", Categories.EATING_OUT);
    desc2CategoryMap.put("bank ", Categories.WITHDRAWAL);
    desc2CategoryMap.put("pepe piri piri ", Categories.EATING_OUT);
    desc2CategoryMap.put("next ", Categories.SHOPPING);
    desc2CategoryMap.put("cafe ", Categories.EATING_OUT);
    desc2CategoryMap.put("affinity ", Categories.UTILITIES);
    desc2CategoryMap.put("iceland ", Categories.GROCERIES);
    desc2CategoryMap.put("screwfix ", Categories.DIY);
    desc2CategoryMap.put("robert dyas ", Categories.HOME_IMPROV);
    desc2CategoryMap.put("luxury4less ", Categories.HOME_IMPROV);
    desc2CategoryMap.put("petroleum ", Categories.FUEL);
    desc2CategoryMap.put("petrol ", Categories.FUEL);
    desc2CategoryMap.put("dental ", Categories.HEALTH);
    desc2CategoryMap.put("netflix ", Categories.TV);
    desc2CategoryMap.put("sky ", Categories.TV);
    desc2CategoryMap.put("lidl ", Categories.GROCERIES);
    desc2CategoryMap.put("post office ", Categories.POSTAGE);
    desc2CategoryMap.put("collectplus ", Categories.POSTAGE);
    desc2CategoryMap.put("sports direct ", Categories.SHOPPING);
    desc2CategoryMap.put("the gym ", Categories.HEALTH);
    desc2CategoryMap.put("churchill insurance. ", Categories.INSURANCE);
    desc2CategoryMap.put("amzn ", Categories.SHOPPING);
    desc2CategoryMap.put("wickes ", Categories.DIY);
    desc2CategoryMap.put("just-eat ", Categories.EATING_OUT);
    desc2CategoryMap.put("creditexpert ", Categories.BILLS);
    desc2CategoryMap.put("chiropractic ", Categories.HEALTH);
    desc2CategoryMap.put("wilko ", Categories.HOME_IMPROV);
    desc2CategoryMap.put("spice village ", Categories.EATING_OUT);
    desc2CategoryMap.put("swinton ", Categories.INSURANCE);
    desc2CategoryMap.put("caffe ", Categories.EATING_OUT);
    desc2CategoryMap.put("bp bessborough ", Categories.FUEL);
    desc2CategoryMap.put("money claim online ", Categories.DIY);
    desc2CategoryMap.put("coffee ", Categories.EATING_OUT);
    desc2CategoryMap.put("spudulike ", Categories.EATING_OUT);
    desc2CategoryMap.put("holland and barrett ", Categories.HEALTH);
    desc2CategoryMap.put("debenhams ", Categories.SHOPPING);
    desc2CategoryMap.put("pizza ", Categories.EATING_OUT);
    desc2CategoryMap.put("harveys", Categories.HOME_IMPROV);
    desc2CategoryMap.put("expedia ", Categories.HOLIDAY);
    desc2CategoryMap.put("gatwickairport", Categories.HOLIDAY);
    desc2CategoryMap.put("airlines ", Categories.HOLIDAY);
    desc2CategoryMap.put("london borough of harr ", Categories.BILLS);
    desc2CategoryMap.put("audible ", Categories.ENTERTAINMENT);
    desc2CategoryMap.put("shell ", Categories.FUEL);
    desc2CategoryMap.put("ebay ", Categories.SHOPPING);
    desc2CategoryMap.put("parking ", Categories.PARKING);
  }

  public String getCategoryForDesc(String desc) {
    String retVal = "UNKNOWN";

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