::ID приложения
SET APP_ID=%1

::Путь к папке с исходным проектом
SET PROJECT_DIR=..\Hobby

::Путь к временной папке с новым проектом
SET TEMP_DIR=..\Hobby_temp


cd %USERPROFILE%\Desktop\PlayLoader

::Копирование новой папки с проектом
::rd /s /q %TEMP_DIR%
::xcopy %PROJECT_DIR% %TEMP_DIR%  /e /y /i /q

::Компиляция и запуск программы по смене пакета
javac -d bin -classpath lib\gson-2.3.1.jar -sourcepath src src\com\ChangeProjectPackage.java
java -classpath bin;lib\gson-2.3.1.jar com.ChangeProjectPackage %APP_ID% %TEMP_DIR%


::Компиляция проекта
cd %TEMP_DIR%
call gradlew assembleRelease --stacktrace

::Загрузка APK файла на google play
%SystemRoot%\explorer.exe "%TEMP_DIR%\app\build\outputs\apk"
start "" https://play.google.com/apps/publish/?hl=ru&dev_acc=09317918585508722696#AppListPlace


:exit
pause
exit
