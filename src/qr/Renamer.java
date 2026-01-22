package qr;
import java.util.*;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import static qr.QuickRenamer.*;

class Renamer  {
    ArrayList<File> container = new ArrayList<File>();

    private static Integer renamed = 0, ignored = 0;
    private static Integer count = 1;

    void rename() throws NullPointerException {
        File folder = new File(getPath());
        File[] images = folder.listFiles();

        container = (ArrayList<File>) Arrays.stream(Objects.requireNonNull(images))
                .filter(File::exists)
                .filter(File::isFile)
                .map(Renamer::toUuid)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        container.sort(Comparator.comparingLong(File::lastModified));
        if (reverseOrder()) {
            Collections.reverse(container);
        }

        count = addNewCount() ? setNewCount() : 1;

        container.forEach(Renamer::toFinalName);
    }

    // Присовить файлу уникальное имя
    private static File toUuid(File file) {
        StringBuilder tempName = new StringBuilder();
        String format = getFormat(file);
        String uuidName = UUID.randomUUID().toString();

        tempName.append(file.getAbsolutePath()).append(uuidName).append(format);

        File tempFile = new File(tempName.toString());

        boolean isRenamed = file.renameTo(tempFile);

        System.out.println("Temp name " + tempName);

        return isRenamed ? tempFile : null;
    }

    // Итоговое переименование
    private static void toFinalName(File file)  {
        if (file != null && file.exists() && file.isFile()) {
            StringBuilder finalName = new StringBuilder();
            String format = addNewFormat() ? getNewFormat() : getFormat(file);
            String baseName;

            try {
                baseName = (onlyDefaultSettings()) ? null : constructFinalName(file);
            }
            catch (IOException e) {
                baseName = null;
            }

            if (baseName == null) {
                baseName = getPath() + getModifiedName() + getCount();
            }
            finalName.append(baseName).append(format);

            System.out.println("name "+file.getName());
            System.out.println("path "+file.toPath());
            System.out.println("format "+ format);
            System.out.println("final name  "+ finalName.toString());

            File finalFile = new File(finalName.toString());

            boolean isRenamed = file.renameTo(finalFile);

            System.out.println("Final name" + isRenamed);

            if (isRenamed) {
                renamedUp();
                count++;
            }
        }
    }

    private  static String getFormat(File file) {
        String format = file.getName().substring(file.getName().lastIndexOf("."));

        return format;
    }

    // Собрать имя с дополнительными настройками
    private static String constructFinalName(File image) throws IOException {
        String constructedFinalName = getPath() + getModifiedName() + getCount() + " ";

        String size = getFileSize(image);
        String dateName = getFileDate(image);

        String toDate = dateName.substring(0, 8); //20121514020520
        String toTime = dateName.substring(8);

        StringBuilder date = new StringBuilder(toDate);
        date.insert(4, "-");
        date.insert(7, "-");

        StringBuilder time = new StringBuilder(toTime); //060154
        time.insert(2, "-");
        time.insert(5, "-");

        if (date() && !time() && !size()) {
            constructedFinalName += date;
            return constructedFinalName;
        }
        else if (time() && !date() && !size()) {
            constructedFinalName +=  time;
            return constructedFinalName;
        }
        else if (size() && !date() && !time()) {
            constructedFinalName +=  size;
            return constructedFinalName;
        }
        else if (date() & time() & !size()) {
            constructedFinalName += date + " " + time;
            return constructedFinalName;
        }
        else if (size() & date() & !time()) {
            constructedFinalName += date + " " + size;
            return constructedFinalName;
        }
        else if (size() & time() & !date()) {
            constructedFinalName += time + " " + size;
            return constructedFinalName;
        }
        else if (size() & date() & time()) {
            constructedFinalName += date + " " + time + " " +size;
            return constructedFinalName;
        }

        return null;
    }

    // Извлечь дату
    private static String getFileDate(File file) throws IOException{
        // Определение даты
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

        Path filePatch = file.toPath();

        BasicFileAttributes attr = Files.readAttributes(filePatch, BasicFileAttributes.class);

        FileTime date = attr.lastModifiedTime();

        return dateFormat.format(date.toMillis());
    }

    private static String getFileSize(File image) {
        long size;
        String finalSize;

        File file = new File(getPath() + image.getName());
        size = file.length() / 1024; // КБ

        if (size > 0 && size <= 1024 ) {
            finalSize = size + "kb";
            return finalSize;
        }
        else if (size > 1024 && size <= 1048576) {
            size /= 1024;
            finalSize = size + "mb";
            return finalSize;
        }
        else if (size > 1048576) {
            size /= (1024*1024);
            finalSize = size + "gb";
            return finalSize;
        }

        return null;
    }

    // Отображение статистических данных
    void showStats(ArrayList<File> container) {
        String path = getPath();
        System.out.println("\nFinished!" + "\nDir: " + path);

        int cs = container.size();
        System.out.println("Images in array: " + cs);
        System.out.println("Renamed: " + renamed + "\nIgnored: " + ignored);

        System.out.println("\nNew name is: " + getModifiedName());

        System.out.println("New format is: " + getNewFormat() + "\nNew count is: " + setNewCount());

        System.out.println("\nAdd size: " + size() + "\nAdd date: " + date() + "\nAdd time: " + time() + "\nReverse: " + reverseOrder());

        ignored = renamed = 0;
        count = 1;
    }

    // Геттеры и счетчики
    private static int getCount() {
        return count;
    }
    private static void renamedUp() {
        renamed++;
    }
    private static void ignoredUp() {
        ignored++;
    }
}
