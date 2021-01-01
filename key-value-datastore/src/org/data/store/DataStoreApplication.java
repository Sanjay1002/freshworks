package org.data.store;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class DataStoreApplication {
	
	static final Scanner reader = new Scanner(System.in);

	public static void main(String[] args) {
		String filePath = null;
		if(args == null || args.length == 0 ||
				args[0] == null || args[0].isEmpty()
				|| args[0].trim().isEmpty()) {
			filePath = "D:\\datastore";
		}else {
			filePath = args[0];
		}
		try {
			Files.createDirectories(Paths.get(filePath));
			filePath = filePath+"\\data.txt";
			File myFile = new File(filePath);
			if (myFile.createNewFile()){
				System.out.println("File is created!");
			}else{
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int exit = 1;
		while(exit == 1) {
			System.out.print("Enter the option : \n"
					+ "1 - create\n"
					+ "2 - read\n"
					+ "3 - delete\n");
			int operator = reader.nextInt();
			System.out.print("Please enter the key: \n");
			String key = reader.next();
			switch (operator) {
			case 1:
				create(filePath, key);
				break;
			case 2:
				read(filePath, key);
				break;
			case 3:
				delete(filePath, key);
				break;
			default:
				System.out.println("Invalid Option");
				break;
			}
			checkFileSize(filePath);
			System.out.println("Enter the option:\n"
					+ "1 - Continue\n"
					+ "0 - Exit \n");
			exit = Integer.parseInt(reader.next());
		}
		reader.close();
	}
	
	public static void checkFileSize(String filePath) {
		try {
			long sizeinGB = Files.size(Paths.get(filePath))/(1024*1024*1024);
			if(sizeinGB >= 1) {
				System.out.println("Data store file size exceeds 1GB");
				System.exit(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void create (String path,String key) {
		boolean keyExist = read(path, key);
		if(keyExist) {
			System.out.println("Key already exists");
			return;
		}
		FileWriter fileWriter = null;
		try {    
			fileWriter = new FileWriter(path,true);
			System.out.println("Please enter the Person Details as Name,age,TimetoLive :"
					+ "For eg: Ram,21,100");
			String input = reader.next();
			String name = input.split(",")[0];
			int age = Integer.parseInt(input.split(",")[1]);
			int timetoLive = 0;
			if(input.split(",").length > 2) {
				timetoLive = Integer.parseInt(input.split(",")[2]);
			}
			String createDate = getCurrentDateandTime();
			Person person = new Person(name, key, age,createDate,timetoLive);
			JSONObject json = new JSONObject(person);
			JSONObject json1 = new JSONObject();
			json1.put(key,json);
			fileWriter.write(json1.toString()+ System.getProperty("line.separator"));
			fileWriter.flush();    
			fileWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Unable to write data to file"+ex);
			throw new RuntimeException("Unable to write data to file");
		}
	}

	public static String getCurrentDateandTime() {
		return ZonedDateTime.now(ZoneOffset.UTC).toString();
	}

	public static long diffTimeInSec(String start){
		String end = getCurrentDateandTime();
		return Instant.parse(end).getEpochSecond() - Instant.parse(start).getEpochSecond();
	}

	public static boolean read (String path,String key) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(path));
			if(!scanner.hasNext()){
				System.out.println("Data Store is empty ");
				return false;
			}
			while (scanner.hasNext()) {
				JSONObject obj = new JSONObject(scanner.nextLine());
				try {
					JSONObject person = (JSONObject) obj.get(key);
					if(Integer.parseInt(person.get("timeToLive").toString()) > 0) {
						int timeToLive = Integer.parseInt(person.get("timeToLive").toString());
						if (diffTimeInSec(person.get("createdDate").toString()) > timeToLive  ) {
							System.out.println("Read Not Available - Time to live has expired");
							return true;
						}
					}
					System.out.println(obj.get(key));
					return true;
				}catch(JSONException e) {

				}
			}
			System.out.println("Data for Key not found");
			return false;
		}catch(Exception ex) {
			System.out.println( "Error reading file '" 
					+ path + "'"+ex);  
		}finally {
			scanner.close();
		}
		return false;
	}

	public static void delete (String path, String key) {
		Scanner scanner = null;
		List<String> out = new ArrayList<String>(); 
		FileWriter fileWriter = null;
		try {
			scanner = new Scanner(new File(path));
			if(!scanner.hasNext()){
				System.out.println("Data Store is empty ");
				return;
			}
			while (scanner.hasNext()) {
				JSONObject obj = new JSONObject(scanner.nextLine());
				try {
					JSONObject person = (JSONObject) obj.get(key);
					if(Integer.parseInt(person.get("timeToLive").toString()) > 0) {
						int timeToLive = Integer.parseInt(person.get("timeToLive").toString());
						if (diffTimeInSec(person.get("createdDate").toString()) > timeToLive  ) {
							System.out.println("Delete Not Available - Time to live has expired");
							return;
						}
					}
				}catch(JSONException e) {
					out.add(obj + System.getProperty("line.separator"));
				}
			}
			fileWriter = new FileWriter(path,false);
			for(String o :out) {
				fileWriter.append(o);
			}
			fileWriter.flush();  
			fileWriter.close();
		}catch(Exception ex) {
			System.out.println("Error deleting file '"+ path + "'"+ex);  
		}finally {
			scanner.close();
		}
	}

}
