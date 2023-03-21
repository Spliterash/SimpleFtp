# Simple FTP server

The latest version can be downloaded [here](https://jenkins.spliterash.ru/job/public/job/SimpleFtp/)

Have you ever tried to set up a ftp server? And in a way that only certain folders can be accessed. I tried, and
eventually decided to write my own server with the ability to mount folders like in docker

Advantages:

* ULTRA simple configuration
* Probably a disadvantage for some, but if you run the process from root you don't have to bother with linux permissions
* Easy user setup
  * Virtual Folders
  * Exclude

Example config

```yaml
server:
  port: 21
  passive-ports: 11000-12000
  address: localhost
users:
  - name: user1
    password: 1234
    # Mounts like docker
    mounts:
        # Host folder : ftp folder
      - "test:/ftp_folder"
  - name: user2
    password: 1234
    # Mounts like docker
    mounts:
      # Host folder : ftp folder
      - "folder1:/stuff"
    # RegEX to exclude files
    # Use virtual path
    excludes:
      - /ftp_folder/secret\.txt
```
