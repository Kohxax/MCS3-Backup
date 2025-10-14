## MCS3-Backup
Minecraftサーバーのワールドをバックアップし、S3にアップロードすることができるSpigotプラグインです。

### 機能
- ワールドのバックアップを作成（cron形式でスケジュール可能）
- 作成したバックアップの自動削除（configから保存数設定可能）
- バックアップしたものをS3にアップロード（認証情報、on/offを設定可能）

### インストール方法
1. [Releases](https://github.com/Kohxax/MCS3-Backup/releases)から最新のjarファイルをダウンロードし、pluginsフォルダに配置する。
2. サーバーを再起動する。
3. `plugins/MCS3-Backup/config.yml`を編集し、必要な設定を行う。

### 設定項目
```yaml
backup-time: "0 0 * * *" # cron形式でバックアップのスケジュールを設定
backup-worlds: # バックアップするディレクトリのリスト(rootディレクトリからの相対パス)
  - world
  - world_nether
  - world_the_end
backup-keep-count: 1 # 保存するバックアップの数

S3: # S3設定方法については下で解説
  enabled: false # S3へのアップロードを有効にするかどうか
  bucket: "your-bucket-name" 
  region: "ap-northeast-1"
  storage-class: "STANDARD"
  upload-prefix: "backups/" #/はなしでもありでも可

  access-key: "your-access-key"
  secret-access-key: "your-secret-access-key"
```

### S3設定方法
1. IAMで`s3:PutObject`の権限を持つユーザーを作成し、アクセスキーとシークレットアクセスキーを取得する。
2. S3バケットを作成し、バケット名とリージョンを確認する。
3. `config.yml`のS3セクションを編集し、取得した情報を入力する。
4. 必要に応じて、`storage-class`や`upload-prefix`を設定する。
5. サーバーを再起動する。

### 追加予定機能
- コマンドで手動バックアップ
- コマンドによるコンフィグリロード
- バックアップのリスト表示と削除
- バックアップのダウンロードリンク生成
- S3バケット内のバックアップ自動削除

### ライセンス
MCS3-BackupはMITライセンスの下で公開されています。

## MCS3-Backup

A Spigot plugin that can back up Minecraft server worlds and upload them to S3.

### Features
- Create backups of worlds (schedules configurable using cron format)
- Automatic deletion of old backups (number of backups to keep configurable in config.yml)
- Upload backups to S3 (authentication and enable/disable configurable)

### Installation
1. Download the latest .jar file from [Releases](https://github.com/Kohxax/MCS3-Backup/releases)
2. and place it in the plugins folder.
3. Restart the server. 
4. Edit plugins/MCS3-Backup/config.yml and configure the settings as needed.

### Configuration
```yaml
backup-time: "0 0 * * *" # Schedule backups using cron format
backup-worlds: # List of directories to back up (relative to the root directory)
- world
- world_nether
- world_the_end
backup-keep-count: 1 # Number of backups to keep

S3: # See S3 setup instructions below
enabled: false # Enable or disable uploading to S3
bucket: "your-bucket-name"
region: "ap-northeast-1"
storage-class: "STANDARD"
upload-prefix: "backups/" # Slash at the end is optional

access-key: "your-access-key"
secret-access-key: "your-secret-access-key"
```

### S3 Setup
1. Create an IAM user with `s3:PutObject` permissions and obtain the Access Key and Secret Access Key.
2. Create an S3 bucket and note the bucket name and region.
3. Edit the S3 section in `config.yml` and enter the obtained credentials.
4. Optionally, configure the `storage-class` and `upload-prefix`.
5. Restart the server.

### Planned Features
- Manual backup via command
- Reload configuration via command
- List and delete backups
- Generate download links for backups
- Automatic deletion of backups in the S3 bucket

### License
MCS3-Backup is released under the MIT License.