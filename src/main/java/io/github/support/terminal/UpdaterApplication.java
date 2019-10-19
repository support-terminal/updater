package io.github.support.terminal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@Slf4j
@SpringBootApplication
public class UpdaterApplication {

    private static String BACKUP_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".support-terminal" + File.separator +"backup"+ File.separator;



    public static void main(String[] args) throws IOException {
     try {
         SpringApplication.run(UpdaterApplication.class, args);

         log.info("Updater запущен. Получены следующие параметры запуска");

         String newAppFilePath = args[0];
         log.info("newAppFilePath " + newAppFilePath);
         String currentApplicationRoot = args[1];
         log.info("currentApplicationRoot " + currentApplicationRoot);
         String oldJarPath = args[2];
         log.info("oldJarPath " + oldJarPath);


         createPathWithParentsOrClear(BACKUP_FILE_PATH);
         log.info("Чистим папку бекапа - Успешно");


         File oldJar = new File(oldJarPath);
         File newJar = new File(newAppFilePath);


         FileUtils.copyFileToDirectory(oldJar, new File(BACKUP_FILE_PATH));
         log.info("Бекап - Успешно");


         FileUtils.forceDelete(oldJar);
         log.info("Удаляем старый jar - Успешно");

         FileUtils.copyFileToDirectory(newJar, new File(currentApplicationRoot));
         log.info("Копия нового jar - Успешно");


         StringBuilder commandBuilder = new StringBuilder();
         commandBuilder.append("java -jar").append(" ")
                 //запускаем апдейтер
                 .append(currentApplicationRoot).append(File.separator)
                 //передаем новый файл
                 .append(newJar.getName());

         log.info("Запуск нового jar - " + commandBuilder.toString());
         Process proc = Runtime.getRuntime().exec(commandBuilder.toString());

         log.info("Выпиливаемся");
         System.exit(0);
     }catch (Exception ex){
         log.error("Ошибка обновления ", ex);
     }

    }

    private static File createPathWithParentsOrClear(String path) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.mkdir();
        FileUtils.cleanDirectory(file);
        return file;
    }

}
