@ECHO OFF

SET config_file_path=%1

IF "%1"=="" (
    SET config_file_path="src\main\resources\config.json"
)

java -cp target\varys-0.1.0-SNAPSHOT.jar org.varys.App %config_file_path%