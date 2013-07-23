package org.essembeh.airvantagefs;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.essembeh.airvantagefs.fs.Watcher;
import org.essembeh.airvantagefs.session.AmsSession;
import org.essembeh.airvantagefs.session.User;

public class AirvantageFS {

	public static void printUsage() {
		System.out.println("Arguments: <system> <password> <rootfolder>");
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
		} else {
			User user = new User(args[0], args[1]);
			Path root = FileSystems.getDefault().getPath(args[2]);
			Watcher watcher = new Watcher(new AmsSession(user), root);
			try {
				watcher.start();
			} catch (Exception e) {
				e.printStackTrace();
				printUsage();
			}
		}
	}
}
