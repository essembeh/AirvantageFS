package org.essembeh.airvantagefs.fs;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.essembeh.airvantagefs.session.AmsSession;

public class Watcher {

	private final AmsSession session;
	private final Path rootPath;

	public Watcher(AmsSession session, Path rootFolder) {
		this.session = session;
		this.rootPath = rootFolder;
	}

	public void start() throws Exception {

		ExecutorService executorService = Executors.newCachedThreadPool();
		final FileSystem fileSystem = FileSystems.getDefault();
		final WatchService watchService = fileSystem.newWatchService();
		final Map<WatchKey, Path> keys = new ConcurrentHashMap<>();

		walk(rootPath, keys, watchService);
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("START");
				while (Thread.interrupted() == false) {
					WatchKey key;
					try {
						key = watchService.poll(10, TimeUnit.MILLISECONDS);
					} catch (InterruptedException | ClosedWatchServiceException e) {
						break;
					}
					if (key != null) {
						Path path = keys.get(key);
						for (WatchEvent<?> i : key.pollEvents()) {
							WatchEvent<Path> event = cast(i);
							WatchEvent.Kind<Path> kind = event.kind();
							Path name = event.context();
							Path child = path.resolve(name);
							System.out.printf("%s: %s %s%n", kind.name(), path, child);
							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
								if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
									try {
										walk(child, keys, watchService);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
							try {
								if (Files.isRegularFile(child)) {
									String content = "";
									if (kind != StandardWatchEventKinds.ENTRY_DELETE) {
										content = FileUtils.readFileToString(child.toFile());
									}
									session.pushContent(getNodeName(child, rootPath), content);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (key.reset() == false) {
							System.out.printf("%s is invalid %n", key);
							keys.remove(key);
							if (keys.isEmpty()) {
								break;
							}
						}
					}
				}
				System.out.println("END");
			}
		});
		while (true) {
		}
	}

	protected String getNodeName(Path resource, Path root) {
		String nodePath = root.relativize(resource).toString();
		nodePath = nodePath.replaceFirst("^\\.", "").replaceAll("\\.", "_").replaceAll("/", ".");
		return nodePath;
	}

	private void walk(Path root, final Map<WatchKey, Path> keys, final WatchService ws) throws IOException {
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir, keys, ws);
				return super.preVisitDirectory(dir, attrs);
			}
		});
	}

	private void register(Path dir, Map<WatchKey, Path> keys, WatchService ws) throws IOException {
		WatchKey key = dir.register(ws, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}
}
