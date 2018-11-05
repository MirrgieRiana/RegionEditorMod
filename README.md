# RegionEditor

巨大な地図を読み込み、チャンク単位で領域を編集し、Dynmap-Forgeで領域を生成するためのコマンドを生成するMinecraftクライアントMOD

## 開発

### Eclipseでの開き方

- `gradlew setupDecompWorkspace`
- `gradlew eclipse`
- リポジトリがEclipseプロジェクトになるのでこれをインポートする

### Eclipseでの起動方法

- Eclipseで`RegionEditorMod_Client.launch`を右クリックして実行

### コンパイル方法

- `gradlew build`

## Usage

### RegionEditorダイアログ呼び出し方

- 金床で木の棒に`RegionEditor.show`という名前を付ける
- それを持って右クリックする
