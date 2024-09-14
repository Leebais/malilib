package malilib.util;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;

import malilib.MaLiLib;
import malilib.config.value.FileWriteType;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.game.wrap.GameWrap;

public class FileUtils
{
    public static final Predicate<Path> DIRECTORY_FILTER = FileUtils::isRegularDirectory;
    public static final Predicate<Path> ALWAYS_FALSE_FILEFILTER = p -> false;
    public static final Predicate<Path> ANY_FILE_FILEFILTER = Files::isRegularFile;
    public static final Predicate<Path> JSON_FILEFILTER = (f) -> Files.isRegularFile(f) && f.getFileName().toString().endsWith(".json");

    public static Path getMinecraftDirectory()
    {
        return GameWrap.getClient().runDir.toPath();
    }

    public static Path getRootDirectory()
    {
        return Paths.get("/");
    }

    public static long size(Path file)
    {
        try
        {
            return Files.size(file);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static long getMTime(Path file)
    {
        try
        {
            return Files.getLastModifiedTime(file).toMillis();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static boolean createDirectoriesIfMissing(Path dir)
    {
        return createDirectoriesIfMissing(dir, MaLiLib.LOGGER::warn);
    }

    public static boolean createDirectoriesIfMissing(Path dir,
                                                     Consumer<String> messageConsumer)
    {
        return createDirectoriesIfMissing(dir, messageConsumer, "Failed to create the directory '%s'");
    }

    public static boolean createDirectoriesIfMissing(Path dir,
                                                     @Nullable Consumer<String> messageConsumer,
                                                     @Nullable String message)
    {
        try
        {
            if (Files.isDirectory(dir) == false)
            {
                Files.createDirectories(dir);
            }
        }
        catch (Exception e)
        {
            if (messageConsumer != null && message != null)
            {
                messageConsumer.accept(String.format(message, dir.toAbsolutePath()));
            }

            return false;
        }

        return Files.isDirectory(dir);
    }

    public static boolean createFile(Path file)
    {
        return createFile(file, MaLiLib.LOGGER::warn);
    }

    public static boolean createFile(Path file, Consumer<String> messageConsumer)
    {
        return createFile(file, messageConsumer, "Failed to create the file '%s'");
    }

    public static boolean createFile(Path file,
                                     @Nullable Consumer<String> messageConsumer,
                                     @Nullable String message)
    {
        try
        {
            Files.createFile(file);
            return true;
        }
        catch (Exception e)
        {
            if (messageConsumer != null && message != null)
            {
                messageConsumer.accept(String.format(message, file.toAbsolutePath()));
            }
        }

        return false;
    }

    public static boolean copy(Path srcFile, Path dstFile)
    {
        return copy(srcFile, dstFile, true);
    }

    public static boolean copy(Path srcFile, Path dstFile, boolean overwrite)
    {
        return copy(srcFile, dstFile, overwrite, MaLiLib.LOGGER::warn);
    }

    public static boolean copy(Path srcFile, Path dstFile, boolean overwrite, Consumer<String> messageConsumer)
    {
        try
        {
            if (overwrite)
            {
                Files.copy(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
            else if (Files.exists(dstFile))
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.file_or_directory_already_exists",
                                                             dstFile.toAbsolutePath()));
            }
            else
            {
                Files.copy(srcFile, dstFile, StandardCopyOption.COPY_ATTRIBUTES);
            }

            return true;
        }
        catch (Exception e)
        {
            messageConsumer.accept(String.format("Failed to copy file '%s' to '%s'\n%s",
                                                 srcFile.toAbsolutePath(), dstFile.toAbsolutePath(), e));
            return false;
        }
    }

    public static boolean move(Path srcFile, Path dstFile)
    {
        return move(srcFile, dstFile, true);
    }

    public static boolean move(Path srcFile, Path dstFile, boolean overwrite)
    {
        return move(srcFile, dstFile, overwrite, MaLiLib.LOGGER::warn);
    }

    public static boolean move(Path srcFile, Path dstFile, boolean overwrite, Consumer<String> messageConsumer)
    {
        try
        {
            if (overwrite)
            {
                Files.move(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
            }
            else if (Files.exists(dstFile))
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.file_or_directory_already_exists",
                                                             dstFile.toAbsolutePath()));
            }
            else
            {
                Files.move(srcFile, dstFile);
            }

            return true;
        }
        catch (Exception e)
        {
            messageConsumer.accept(String.format("Failed to move file '%s' to '%s'",
                                                 srcFile.toAbsolutePath(), dstFile.toAbsolutePath()));
            return false;
        }
    }

    public static boolean delete(Path file)
    {
        return delete(file, MaLiLib.LOGGER::warn);
    }

    public static boolean delete(Path file, Consumer<String> messageConsumer)
    {
        try
        {
            Files.delete(file);
            return true;
        }
        catch (Exception e)
        {
            messageConsumer.accept(String.format("Failed to delete file '%s'", file.toAbsolutePath()));
            return false;
        }
    }

    public static boolean copyFileWithMessage(Path oldFile, Path newDir, String newName)
    {
        String originalFileName = oldFile.getFileName().toString();
        String extension = FileNameUtils.getFileNameExtension(originalFileName);

        if (extension.length() > 0)
        {
            extension = "." + extension;
        }

        Path newFile = newDir.resolve(newName + extension);
        return copyFileWithMessage(oldFile, newFile);
    }

    public static boolean copyFileWithMessage(Path oldFile, Path newFile)
    {
        return copy(oldFile, newFile, false, MessageDispatcher.error(8000)::send);
    }

    public static boolean moveFileWithMessage(Path oldFile, Path newDir, String newName)
    {
        String originalFileName = oldFile.getFileName().toString();
        String extension = FileNameUtils.getFileNameExtension(originalFileName);

        if (extension.length() > 0)
        {
            extension = "." + extension;
        }

        Path newFile = newDir.resolve(newName + extension);
        return moveFileWithMessage(oldFile, newFile);
    }

    public static boolean moveFileWithMessage(Path oldFile, Path newFile)
    {
        return move(oldFile, newFile, false, MessageDispatcher.error(8000)::send);
    }

    /**
     * Checks that the target directory exists, and the file either doesn't exist,
     * or the canOverwrite argument is true and the file is writable
     */
    public static boolean canWriteToFile(Path dir, String fileName, boolean canOverwrite)
    {
        if (Files.isDirectory(dir))
        {
            Path file = dir.resolve(fileName);
            return Files.exists(file) == false ||
                   (canOverwrite && Files.isRegularFile(file) && Files.isWritable(file));
        }

        return false;
    }

    public static boolean writeDataToFile(final Path file, Consumer<BufferedWriter> dataWriter, FileWriteType writeType)
    {
        Path dir = file.getParent();

        if (dir != null && createDirectoriesIfMissing(dir) == false)
        {
            return false;
        }

        if (dir == null)
        {
            dir = Paths.get(".");
        }

        if (writeType == FileWriteType.NORMAL_WRITE || Files.isSymbolicLink(file))
        {
            // Don't replace/override symbolic links, but just write to the pointed file directly
            return writeDataToExactFile(file, dataWriter);
        }

        if (writeType == FileWriteType.TEMP_AND_RENAME)
        {
            // First write to a separate temporary file, and then rename it over the old file
            Path fileTmp = dir.resolve(file.getFileName() + ".tmp");

            if (Files.exists(fileTmp))
            {
                fileTmp = dir.resolve(UUID.randomUUID() + ".tmp");
            }

            return writeDataToExactFile(fileTmp, dataWriter) && move(fileTmp, file);
        }

        return false;
    }

    public static boolean writeDataToExactFile(final Path file, Consumer<BufferedWriter> dataWriter)
    {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
        {
            dataWriter.accept(writer);
            writer.close();
            return true;
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to write to file '{}'", file.toAbsolutePath(), e);
        }

        return false;
    }

    public static boolean isRegularDirectory(Path file)
    {
        String name = file.getFileName() + ""; // cheap & lazy NPE protection...
        return Files.isDirectory(file) && name.equals(".") == false && name.equals("..") == false;
    }

    public static boolean isCurrentOrParentDirectory(Path file)
    {
        String name = file.getFileName() + ""; // cheap & lazy NPE protection...
        return Files.isDirectory(file) && (name.equals(".") || name.equals(".."));
    }

    public static List<Path> getDirectoryContents(Path dir, Predicate<Path> filter, boolean sortByName)
    {
        List<Path> list = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir))
        {
            for (Path file : stream)
            {
                if (filter.test(file))
                {
                    list.add(file);
                }
            }

            if (sortByName)
            {
                list.sort(Comparator.comparing(Path::getFileName));
            }
        }
        catch (Exception ignore) {}

        return list;
    }

    /**
     * @param fileNameFormatString the format string for {@link String#format(String, Object... args)}.
     *                             A single int argument will ne given to the format call.
     * @return the first unused file name within the directory {@code dir}, using a running number,
     *         starting from {@code startNumber}.
     */
    public static Path getUnusedFileName(Path dir, String fileNameFormatString, int startNumber)
    {
        int id = startNumber;
        Path file;

        do
        {
            String name = String.format(fileNameFormatString, id);
            file = dir.resolve(name);
            ++id;
        } while (Files.exists(file));

        return file;
    }

    /**
     * @return the relative path of {@code file} to the containing upper directory {@code dir}.
     * If {@code file} is not within the path to {@code dir}, then null is returned.
     */
    @Nullable
    public static String getRelativePath(Path dir, Path file)
    {
        try
        {
            return dir.relativize(file).toString();
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    public static List<Path> getSubDirectories(Path dir)
    {
        return getDirectoryContents(dir, FileUtils::isRegularDirectory, true);
    }

    public static boolean isDirectoryEmpty(Path dir)
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir))
        {
            for (Path file : stream)
            {
                if (isCurrentOrParentDirectory(file) == false)
                {
                    return false;
                }
            }
        }
        catch (Exception ignore) {}

        return true;
    }

    public static List<Path> getDirsForRootPath(Path dir, Path root)
    {
        List<Path> dirs = new ArrayList<>();

        while (dir != null && dir.startsWith(root))
        {
            dirs.add(dir);

            if (root.equals(dir))
            {
                break;
            }

            dir = dir.getParent();
        }

        return dirs;
    }

    public static List<Path> getSiblingDirs(Path dir)
    {
        List<Path> dirs = new ArrayList<>();
        Path parent = dir.getParent();

        if (parent != null)
        {
            dirs.addAll(getSubDirectories(parent));
            dirs.sort(Comparator.comparing(Path::getFileName));
        }

        return dirs;
    }

    public static boolean copyFilesToDirectory(Collection<Path> files,
                                               Path destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (Path file : files)
        {
            if (copyFileToDirectory(file, destinationDir, messageConsumer) == false)
            {
                success = false;
            }
        }

        return success;
    }

    public static boolean copyFileToDirectory(Path sourceFile, Path destinationDir, Consumer<String> messageConsumer)
    {
        Path destinationFile = destinationDir.resolve(sourceFile.getFileName());
        return copyFile(sourceFile, destinationFile, messageConsumer);
    }

    public static boolean copyFile(Path sourceFile, Path destinationFile, Consumer<String> messageConsumer)
    {
        if (Files.exists(sourceFile))
        {
            if (Files.exists(destinationFile))
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_copy_file.destination_exists",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                return false;
            }

            try
            {
                Files.copy(sourceFile, destinationFile, StandardCopyOption.COPY_ATTRIBUTES);
                return true;
            }
            catch (Exception e)
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_copy_file",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                messageConsumer.accept(e.getMessage());
            }
        }

        return false;
    }

    public static boolean moveFilesToDirectory(Collection<Path> files,
                                               Path destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (Path file : files)
        {
            if (moveFileToDirectory(file, destinationDir, messageConsumer) == false)
            {
                success = false;
            }
        }

        return success;
    }

    public static boolean moveFileToDirectory(Path sourceFile, Path destinationDir, Consumer<String> messageConsumer)
    {
        Path destinationFile = destinationDir.resolve(sourceFile.getFileName());
        return moveFile(sourceFile, destinationFile, messageConsumer);
    }

    public static boolean moveFile(Path sourceFile, Path destinationFile, Consumer<String> messageConsumer)
    {
        if (Files.exists(sourceFile))
        {
            if (Files.exists(destinationFile))
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_move_file.destination_exists",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                return false;
            }

            try
            {
                Files.move(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_move_file",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                messageConsumer.accept(e.getMessage());
            }
        }

        return false;
    }

    public static boolean renameFile(Path sourceFile, Path destinationFile, Consumer<String> messageConsumer)
    {
        if (Files.exists(sourceFile))
        {
            if (Files.exists(destinationFile))
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_rename_file.exists",
                                                             sourceFile.toAbsolutePath().toString(),
                                                             destinationFile.toAbsolutePath().toString()));
                return false;
            }

            try
            {
                return move(sourceFile, destinationFile);
            }
            catch (Exception e)
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_rename_file.exception",
                                                             sourceFile.toAbsolutePath().toString(),
                                                             destinationFile.toAbsolutePath().toString(),
                                                             e.getMessage()));
            }
        }

        return false;
    }

    public static boolean renameFileToName(Path oldFile, String newName, Consumer<String> messageConsumer)
    {
        if (FileNameUtils.doesFileNameContainIllegalCharacters(newName))
        {
            String key = "malilib.message.error.illegal_characters_in_file_name";
            messageConsumer.accept(StringUtils.translate(key, newName));
            return false;
        }

        String oldName = oldFile.getFileName().toString();
        int indexExt = oldName.lastIndexOf('.');
        String ext = indexExt > 0 ? oldName.substring(indexExt) : null;

        if (ext != null && newName.endsWith(ext) == false)
        {
            newName = newName + ext;
        }

        Path dir = oldFile.getParent();

        if (dir == null)
        {
            dir = Paths.get(".");
        }

        Path newFile = dir.resolve(newName);

        if (Files.exists(newFile) == false)
        {
            if (move(oldFile, newFile))
            {
                return true;
            }
            else
            {
                String key = "malilib.message.error.file_rename.rename_failed";
                messageConsumer.accept(StringUtils.translate(key, oldName, newName));
            }
        }
        else
        {
            String key = "malilib.message.error.failed_to_rename_file.exists";
            messageConsumer.accept(StringUtils.translate(key, oldName, newName));
        }

        return false;
    }

    public static boolean deleteFiles(Collection<Path> files, Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (Path file : files)
        {
            if (Files.isDirectory(file))
            {
                String key = "malilib.message.error.failed_to_delete_file_is_dir";
                messageConsumer.accept(StringUtils.translate(key, file.getFileName().toString()));
                success = false;
                continue;
            }

            try
            {
                if (Files.deleteIfExists(file) == false)
                {
                    String key = "malilib.message.error.failed_to_delete_file";
                    messageConsumer.accept(StringUtils.translate(key, file.getFileName().toString()));
                    success = false;
                }
            }
            catch (Exception e)
            {
                String key = "malilib.message.error.failed_to_delete_file";
                messageConsumer.accept(StringUtils.translate(key, file.getFileName().toString()));
                messageConsumer.accept(e.getMessage());
                success = false;
            }
        }

        return success;
    }

    /**
     * Reads the given file as a String.
     * @param file the file to read
     * @param maxFileSize only read the file if it's at most this size. Use -1 for no limit.
     * @return the file contents as a String
     */
    @Nullable
    public static String readFileAsString(Path file, int maxFileSize)
    {
        if (Files.isReadable(file) &&
            (maxFileSize == -1 || size(file) <= maxFileSize))
        {
            try
            {
                return String.join("\n", Files.readAllLines(file));
            }
            catch (Exception ignore) {}
        }

        return null;
    }

    public static boolean writeStringToFile(String str, Path file, boolean override)
    {
        Path dir = file.getParent();

        if (dir == null)
        {
            dir = Paths.get(".");
        }

        if (Files.isDirectory(dir) == false)
        {
            return false;
        }

        if (Files.exists(file) == false || (override && Files.isWritable(file)))
        {
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
            {
                writer.write(str);
                writer.close();
                return true;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("Failed to write string to file '{}'", file.toAbsolutePath(), e);
            }
        }

        return false;
    }

    @Nullable
    public static InputStream openModResource(String modId, String path)
    {
        /* TODO in-20100223
        for (ModTexturePack pack : ResourceLoader.getDefaultModResourcePacks())
        {
            if (pack.getModMetadata().getId().equals(modId))
            {
                return pack.getResource(path);
            }
        }
        */

        return MaLiLib.class.getResourceAsStream(String.format("/assets/%s/%s", modId, path));
    }

    @Nullable
    public static InputStream openVanillaResource(String path)
    {
        /* TODO in-20100223
        return GameWrap.getClient().texturePacks.selected.getResource(path);
        */
        return Minecraft.class.getResourceAsStream(path);
    }
}
