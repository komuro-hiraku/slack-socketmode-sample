# Slack Socket Mode Sample

## 開発環境

- IntelliJ IDEA 2020.3.3 (Community Edition)
  Build #IC-203.7717.56, built on March 15, 2021
  Runtime version: 11.0.10+8-b1145.96 x86_64
  VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
  macOS 10.15.7
  GC: ParNew, ConcurrentMarkSweep
  Memory: 1981M
  Cores: 8
  Non-Bundled Plugins: mobi.hsz.idea.gitignore, com.redhat.devtools.intellij.quarkus, org.jetbrains.kotlin, io.ktor.intellij.plugin
- macOS Catalina 10.15.7

## Slack App の作成

[リンク](https://qiita.com/seratch/items/1a460c08c3e245b56441#slack-%E3%82%A2%E3%83%97%E3%83%AA%E3%82%92%E8%A8%AD%E5%AE%9A%E3%81%99%E3%82%8B) の手順を実行します。

### 1. Slack App の作成

- https://api.slack.com/apps?new_app=1 にアクセス。新たなアプリを作成

### 2. Socket Mode を有効にする

- 左の設定タブから「Socket Mode」を選択し、Enableへ
- scope の `connection:write` は必須なので外さない（WebSocket接続ができなくなるそう）
- `Generate` 押下で Token が発行される

### 3. Create Global Shortcut

- `Features > Interactivity & Shortcuts` から `Shortcuts` を選択
- `Create New Shortcut` をクリック
- `Global Shortcut` を選択して Next をクリック
- 必要な情報を埋める
  - 名前
  - 説明
  - Callback ID
    - アプリケーション側でハンドリングする際に利用するので、一意であれば良さそう
  - `Create` をクリック
- Save and Changes をクリックして保存

### 4. Enable Event API

- 左メニューから `Event Subscriptions` を選択
- `Enable Events` を ON に変更
- Bot User が受け取るイベント種別を選択します
  - `message:channels` チャンネルにメッセージが投稿されると受信
  - Bot が受け取りたいイベントに応じて追加します
- Save Changes をクリックして保存

### 5. Bot の権限を確認

- 左メニューから `OAuth & Permissions` を選択
- **Scopes** の Bot Token Scopes に `chat:write` を追加する

## 実行方法

```shell
export SLACK_APP_TOKEN=xapp-<自分のトークンの値>
export SLACK_BOT_TOKEN=xoxb-<自分のトークンの値>
./gradlew clean run
```

起動すると以下の表示になります。

```shell
$ ./gradlew clean run
Starting a Gradle Daemon (subsequent builds will be faster)

> Task :run
[Grizzly(2)] INFO com.slack.api.socket_mode.SocketModeClient - New session is open (session id: d5e680a2-b4d3-4ba5-9e3f-5840a3c0554a)
<==========---> 80% EXECUTING [1m 52s]
> :run
```

## 機能

### Echo message

T.B.D

### Create Channel with Open Dialog

T.B.D


## トラブルシューティング

### Socket Mode の AppToken を控えるのを忘れてしまった

- Slack Application の設定画面へ
- 左メニューの `Basic Information` をクリック
- `App-Level Tokens` のセクションを探す
- すでに Socket Mode の App Token が発行されている場合、 `Token Name` に **socket mode** が表示されている
- 上記リンクをクリックするとダイアログが表示され、 Socket Mode の APP_TOKEN が取得できる


## 参照

- [Qiita - Slack ソケットモードの最も簡単な始め方](https://qiita.com/seratch/items/1a460c08c3e245b56441)