/* $Id: createaccount.java,v 1.1 2004/04/25 01:19:19 arianne_rpg Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package mapacman;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import marauroa.game.*;
import marauroa.Configuration;

class createaccount
  {
  public static void main (String[] args)
    {
    System.exit(createaccount(args));
    }

  public static int createaccount(String[] args)
    {
    /** TODO: Factorize this method */
    int i=0;
    PrintWriter out=null;
    String username=null;
    String password=null;
    String email=null;
    String character=null;
        
    while(i!=args.length)
      {
      if(args[i].equals("-u"))
        {
        username=args[i+1];
        }
      else if(args[i].equals("-p"))
        {
        password=args[i+1];
        }
      else if(args[i].equals("-e"))
        {
        email=args[i+1];
        }
      else if(args[i].equals("-c"))
        {
        character=args[i+1];
        }
      else if(args[i].equals("-h"))
        {
        // TODO: Write help
        }
      ++i;
      }
    if(username==null) return (1);
    if(password==null) return (1);
    if(email==null) return (1);
    if(character==null) return (1);
    
    Transaction trans=null;
      
    try
      {
      Configuration.setConfigurationFile("mapacman.ini");
      Configuration conf=Configuration.getConfiguration();
      
      String webfolder=conf.get("server_logs_directory");

      out=new PrintWriter(new FileOutputStream(webfolder+"/createaccount_log.txt",true));
      out.println(new Date().toString()+": Trying to create username("+username+"), password("+password+"), character("+character+")");
      out.flush();
      
      JDBCPlayerDatabase playerDatabase=(JDBCPlayerDatabase)PlayerDatabaseFactory.getDatabase("JDBCPlayerDatabase");

      trans=playerDatabase.getTransaction();
      out.println("Checking for valid string");
      out.flush();
      if(playerDatabase.validString(username)==false)
        {
        out.println("String not valid: "+username);
        return (2);
        }
      if(playerDatabase.validString(password)==false)
        {
        out.println("String not valid: "+password);
        return (2);
        }
      if(playerDatabase.validString(email)==false)
        {
        out.println("String not valid: "+email);
        return (2);
        }
      if(playerDatabase.validString(character)==false)
        {
        out.println("String not valid: "+character);
        return (2);
        }
      out.println("Checking string size");
      if(username.length()>10 || username.length()<4)
        {
        out.println("String size not valid: "+username);
        return (3);
        }
      if(password.length()>10 || password.length()<1)
        {
        out.println("String size not valid: "+password);
        return (3);
        }
      if(email.length()>50 || email.length()<5)
        {
        out.println("String size not valid: "+password);
        return (3);
        }
      if(character.length()>20 || character.length()<4)
        {
        out.println("String size not valid: "+character);
        return (3);
        }
      out.println("Checking if player exists");
      if(playerDatabase.hasPlayer(trans, username))
        {
        out.println("ERROR: Player exists");
        return (4);
        }
      out.println("Adding player");
      playerDatabase.addPlayer(trans,username,password,email);

      RPObject object=new RPObject(playerDatabase.getValidRPObjectID(trans));
      object.put("type","player");
      object.put("name",character);
      object.put("x",0);
      object.put("y",0);
      object.put("dir","N");
      object.put("score",0);
      
      playerDatabase.addCharacter(trans, username,character,object);
      out.println("Correctly created");
      trans.commit();
      }
    catch(Exception e)
      {
      out.println("Failed: "+e.getMessage());
      e.printStackTrace(out);
      
      try
        {
        trans.rollback();
        }
      catch(Exception ae)
        {
        out.println("Failed Rollback: "+ae.getMessage());
        }
      return (5);
      }
    finally
      {
      if(out!=null)
        {
        out.flush();
        out.close();
        }
      }
    return (0);
    }
  }
