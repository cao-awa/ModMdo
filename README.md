# ModMdo
A fabric mod for secure minecraft server.

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
### Identifier
ModMdo use "pre shared" to exchange identifier to server.

Server only store identifier when first join, it is unable to be overwrites.

This identifier will be check when ModMdo requesting once login, fully equals then mean success of checking.\
<br>

The identifier size is 4096 bytes, was creates 4096^91 possibilities, impossible to try all possibilities.
### Private key
Let server store a encrypted data, for futures verify.\
<br>

ModMdo will send key to decrypt when requesting login, server should not store it.

Server decrypted data, separate out identifier, and equals to current recived identifier then mean success to verify.
### Sha
The server should store the identifier using Sha3-512 hash.

And client should sending source identifier, let server to calculate hash, ensure this identifier are not leaking from the server database.

Verify data should be hashed, not source identifier, for faster verifing.\
<br>

This feature let identifier unable to use when database give away.

### Other
Server will not send these data to other ModMdo client, includes your client.

The database leaking is meaningless, can be ignored.

### MITM
Preparing to use security handshake by Kalmia, pre share an EC-384 public key, and server saving the private key, middle attacker is unable to decrypt any informations.
