import json
import subprocess


launch_json_path = ".vscode/launch.json"
frontend_path = r"\\src\\main\\java\\code\\frontend"
application_path = r"\\src\\main\\java\\code\\app.js"




def launch_backend():
    with open(launch_json_path, "r") as launch_file:
        launch_configurations = json.load(launch_file)
    configuration = launch_configurations["configurations"][0]

    main_class = configuration.get("mainClass")
    arg_1, arg_2 = configuration.get("args", " ")
    args = arg_1 + " " + arg_2

    backend_command = f"mvn exec:java -Dexec.mainClass='{main_class}' -Dexec.args='{args}'"
    subprocess.run(backend_command)



def launch_frontend():
    subprocess.run(f"cd {frontend_path}")
    frontend_command = "python -m http.server 5500"
    application_comand = f"npx electron {application_path}"

    subprocess.run(frontend_command)
    subprocess.run(application_comand)



if __name__ == "__main__":
    launch_backend()
    launch_frontend()
