config_file_path=$1

if [$1 = ""] 
then
    config_file_path="config/config.yml"
fi

java -cp target/varys-0.1.0-SNAPSHOT.jar org.varys.App $config_file_path
