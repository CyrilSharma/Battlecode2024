# Testing using Modal for access to massive amounts of compute
# The container's files sync with your local ones, so make sure
# that you have run ./gradlew update ./gradlew build locally first

import os
import modal
from modal import Image
from tester import parse_results_text, read_maps


stub = modal.Stub()
volume = modal.NetworkFileSystem.persisted("battlecode-vol")
LOCAL_PROJECT_DIR = os.path.join(os.path.dirname(__file__))


def get_info():
    import subprocess

    output = subprocess.run(
        ["ls", "-l", "/usr/lib/jvm/"], capture_output=True, text=True, check=True
    ).stdout
    print("get info:")
    print(output)

def gradlew_setup():
    import subprocess
    
    subprocess.run(
        ["dos2unix", "gradlew"], capture_output=True, check=True
    )
    output = subprocess.run(
        ["bash", "gradlew"], capture_output=True, check=True
    )
    print(output)

# https://askubuntu.com/questions/931610/how-to-install-jdk8-on-ubuntu-16
# https://askubuntu.com/questions/1139387/update-to-latest-version-of-java-after-ppa-is-discontinued
# http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.htmlsudo
# https://stackoverflow.com/questions/10268583/downloading-java-jdk-on-linux-via-wget-is-shown-license-page-instead
# https://docs.datastax.com/en/jdk-install/doc/jdk-install/installOracleJdkDeb.html
image = (
    Image.from_registry("ubuntu:22.04", add_python="3.11")
    .apt_install(["wget"])
    .run_commands(
        "wget -c --header 'Cookie: oraclelicense=accept-securebackup-cookie' http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz",
        "mkdir -p /usr/lib/jvm",
        "tar -zxvf jdk-8u131-linux-x64.tar.gz -C /usr/lib/jvm",
    )
    .run_function(get_info)
    .run_commands(
        'update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_131/bin/java" 1',
        "update-alternatives --set java /usr/lib/jvm/jdk1.8.0_131/bin/java",
    )
    .apt_install(["dos2unix"])
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, ".idea"), remote_path="/root/.idea")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, "gradle"), remote_path="/root/gradle")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, "maps"), remote_path="/root/maps")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, "test"), remote_path="/root/test")
    .copy_local_file(os.path.join(LOCAL_PROJECT_DIR, "build.gradle"), remote_path="/root/build.gradle")
    .copy_local_file(os.path.join(LOCAL_PROJECT_DIR, "gradle.properties"), remote_path="/root/gradle.properties")
    .copy_local_file(os.path.join(LOCAL_PROJECT_DIR, "gradlew"), remote_path="/root/gradlew")
    .copy_local_file(os.path.join(LOCAL_PROJECT_DIR, "version.txt"), remote_path="/root/version.txt")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, "bin"), remote_path="/root/bin")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, "build/libs"), remote_path="/root/build/libs")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, "client"), remote_path="/root/client")
    .copy_local_dir(os.path.join(LOCAL_PROJECT_DIR, ".gradle"), remote_path="/root/.gradle")
    .run_function(gradlew_setup)
)

@stub.function(
    mounts=[
        modal.Mount.from_local_dir(os.path.join(LOCAL_PROJECT_DIR, "src"), remote_path="/root/src"),
    ],
    image=image,
    network_file_systems={"/root/matches_final": volume}
)
def tester(team1: str, team2: str, map: str):
    import subprocess
    import os
    import shutil

    def get_reason(text):
        for line in text:
            if not line.startswith("[server]"):
                continue
            if "Reason: " in line:
                return line.split("Reason: ")[1]

    command = (
        "./gradlew run"
        + f" -PteamA={team1}"
        + f" -PteamB={team2}"
        + f" -Pmaps={map}"
        + f" -Psource=src"
        + f" -PprofilerEnabled=false"
        + f" -PoutputVerbose=false"
    )

    output = subprocess.run(
        command, capture_output=True, check=True, shell=True
    )
    lines = output.stdout.decode().split("\n")
    team1_game1, team2_game1 = parse_results_text(lines)
    print(f"{team1 if team1_game1 else team2} wins on map {map} for reason: {get_reason(lines)}")
    command = (
        "./gradlew run"
        + f" -PteamA={team2}"
        + f" -PteamB={team1}"
        + f" -Pmaps={map}"
        + f" -Psource=src"
        + f" -PprofilerEnabled=false"
        + f" -PoutputVerbose=false"
    )

    output = subprocess.run(
        command, capture_output=True, check=True, shell=True
    )
    lines = output.stdout.decode().split("\n")
    team2_game2, team1_game2 = parse_results_text(lines)
    print(f"{team1 if team1_game2 else team2} wins on map {map} for reason: {get_reason(lines)}")

    for file in os.listdir("/root/matches/"):
        shutil.copyfile("/root/matches/" + file, "/root/matches_final/" + file)

    return team1_game1 + team1_game2, team2_game1 + team2_game2


@stub.local_entrypoint()
def main(team1: str, team2: str):
    tot1 = 0
    tot2 = 0
    maps = read_maps()
    num_games = len(maps)
    for team1_wins, team2_wins in tester.map(
        [team1] * (num_games), [team2] * (num_games), maps
    ):
        tot1 += team1_wins
        tot2 += team2_wins

    print(f"{team1} wins: {tot1}, {team2} wins: {tot2}")
