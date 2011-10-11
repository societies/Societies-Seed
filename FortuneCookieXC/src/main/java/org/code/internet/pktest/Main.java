package org.code.internet.pktest;

import java.util.logging.*;
import org.jivesoftware.whack.*;
import org.xmpp.component.*;

public class Main {
   public static void main(String[] args) {
      ExternalComponentManager mgr = new ExternalComponentManager("red.local", 5275);
      mgr.setSecretKey("landofthegiants", "uppercase");
      try {
         mgr.addComponent("landofthegiants", new UpperCaseComponent());
      } catch (ComponentException e) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "main", e);
         System.exit(-1);
      }
      //Keep it alive
      while (true)
         try {
            Thread.sleep(1000);
         } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "main", e);
         }
   }
}