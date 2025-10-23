import json
import subprocess
import platform
from time import sleep


launch_json_path = ".vscode/launch.json"
frontend_path = "src/main/java/code/frontend"
application_path = "src/main/java/code/app.js"




def launch_backend():
    with open(launch_json_path, "r") as launch_file:
        launch_configurations = json.load(launch_file)
    configuration = launch_configurations["configurations"][0]

    main_class = configuration.get("mainClass")
    arg_1, arg_2 = configuration.get("args", " ")
    args = arg_1 + " " + arg_2

    backend_command = f"mvn maven:java -Dexec.mainClass='{main_class}' -Dexec.args='{args}'"
    subprocess.Popen(backend_command, shell=True, text=True)
    sleep(100/1000) # 100ms
    print("Backend running")



def launch_frontend():
    frontend_command = f"cd '{frontend_path}'" + " && " + "python -m http.server 5500"
    if platform.system() == "Windows":
        frontend_command.replace("&&", "&")
    subprocess.Popen(frontend_command, shell=True, text=True)
    sleep(100/1000) # 100ms
    print("Frontend running")

    application_comand = f"npx electron {application_path}"
    subprocess.Popen(application_comand, shell=True, text=True)
    print("Application running")



if __name__ == "__main__":
    launch_backend()
    launch_frontend()
