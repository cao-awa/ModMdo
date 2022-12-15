# ModMdo
A fabric mod for secure minecraft server.

## Uses
If server is offline, Client need install ModMdo, nothing more.

If not offline, can use Minecraft accounts to replace ModMdo login.

## Command
Enable ModMdo whitelist:

```/modmdo useModMdoWhitelist enable ```

Give whitelist to a player:

```/temporary whitelist add PlayerName ```

## Timed out

If player are not join to server before timed out(usually is 5 minutes),

This player will not be approved by ModMdo, need a temporary whitelist again. 

## Build
ModMdo need environment: ``` Fabric ``` & ``` Fabric API ``` & ``` Java ```.

<br>

Before building, use git clone or download zip to completely download a branch.

<br>

Build project with gradle command:

``` ./gradlew clean build ```

Or use intellij idea gradle tools.

<br>

Build jar with gradle command:

``` ./gradlew clean remapJar ```

## How to secure

