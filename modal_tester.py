# Testing using Modal for access to massive amounts of compute
# The container's files sync with your local ones, so make sure
# that you have run ./gradlew update ./gradlew update locally first

import os
import modal
from modal import Image
from tester import parse_results

def get_info():
    import subprocess

    output = subprocess.run(["ls", "-l", "/usr/lib/jvm/"], capture_output=True, text=True, check=True).stdout
    print(output) 

def query_java():
    # confirms that java installation was successful
    import subprocess

    output = subprocess.run(["java", "-version"], capture_output=True, text=True, check=True).stdout
    print(output)

# https://askubuntu.com/questions/931610/how-to-install-jdk8-on-ubuntu-16
# https://askubuntu.com/questions/1139387/update-to-latest-version-of-java-after-ppa-is-discontinued
# http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.htmlsudo 
# https://stackoverflow.com/questions/10268583/downloading-java-jdk-on-linux-via-wget-is-shown-license-page-instead
# https://docs.datastax.com/en/jdk-install/doc/jdk-install/installOracleJdkDeb.html
image = Image.from_registry("ubuntu:22.04", add_python="3.11").apt_install(["wget"]).run_commands(
    "wget -c --header 'Cookie: oraclelicense=accept-securebackup-cookie' http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz",
    "mkdir -p /usr/lib/jvm",
    "tar -zxvf jdk-8u131-linux-x64.tar.gz -C /usr/lib/jvm",
).run_function(get_info).run_commands(
    'update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_131/bin/java" 1',
    "update-alternatives --set java /usr/lib/jvm/jdk1.8.0_131/bin/java"
    # "apt-get update",
    # "apt-get install -y software-properties-common",
    # "apt-add-repository ppa:ts.sch.gr/ppa",
    # "echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections",
    # "apt-get update",
    # "apt-get install -y -f oracle-java8-set-default",
).run_function(query_java)

stub = modal.Stub() 

volume = modal.NetworkFileSystem.persisted("battlecode-vol")

LOCAL_PROJECT_DIR = os.path.join(os.path.dirname(__file__))

@stub.function(
    mounts=[
        modal.Mount.from_local_dir(
            LOCAL_PROJECT_DIR, remote_path="/"
        ),
    ],
    image=image,
)
def tester(team1: str, team2: str, map: str):
    import subprocess
    ofile = "output"

    command = "./gradlew run"\
            + f" -PteamA={team1}"\
            + f" -PteamB={team2}"\
            + f" -Pmaps={map}"\
            + f" -Psource=src"\
            + f" -PprofilerEnabled=false"\
            + f" -PoutputVerbose=false"
    
    with open(ofile, "w") as f:
        subprocess.run(
            [command], stdout=f, stderr=f
        )
        f.flush()
    team1_game1, team2_game1 = parse_results(ofile)
    
    command = "./gradlew run"\
            + f" -PteamA={team2}"\
            + f" -PteamB={team1}"\
            + f" -Pmaps={map}"\
            + f" -Psource=src"\
            + f" -PprofilerEnabled=false"\
            + f" -PoutputVerbose=false"
    
    with open(ofile, "w") as f:
        subprocess.run(
            [command], stdout=f, stderr=f
        )
        f.flush()
    team2_game2, team1_game2 = parse_results(ofile)

    os.remove(ofile)
    
    return team1_game1 + team1_game2, team2_game1 + team2_game2

@stub.local_entrypoint()
def main(team1: str, team2: str, map: str, num_games: int = 10):
    tot1 = 0
    tot2 = 0
    for team1_wins, team2_wins in tester.map([team1] * (num_games//2), [team2] * (num_games//2), [map] * (num_games//2)):
        tot1 += team1_wins
        tot2 += team2_wins

    print(f"{team1} wins: {tot1}, {team2} wins: {tot2}")