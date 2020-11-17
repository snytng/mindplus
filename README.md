# Mind+プラグイン

## 説明
Mind+はastah*マインドマップのユーティリティプラグインです。
マインドマップのツリーの開閉が簡単にできます。
マインドマップのノードの分割・マージ、文字列の削除などの編集が簡単にできます。

## ダウンロード
- [ここ](https://github.com/snytng/mindplus/releases/latest)から`mindplus-<version>.jar`をダウンロードして下さい。

## インストール
- ダウンロードしたプラグインファイルをastah*アプリケーションにドラッグドロップするか、Program Files\asta-professionals\pluginsに置いて下さい。

## 機能
- ノードの子ノードを開閉の切り替え、全展開、全閉じ、１段ずつ展開、１段ずつ閉じることができます。
- ノードに雲をつけたり、なくしたりできます。
- 複数のノードを一つのノードにマージできます。
- 改行や。でノードを分割できます。
- ノードの中の特定の文字列を削除、置換できます。
- ノードの並び順を逆順にしたり回転したりできます。
- 日付、時間をノードとして追加できます。

## 使い方
- 操作の準備
    - ノードを選択すると各種機能の適用が有効になります（ボタンが有効に変わります）
- ノードの開閉
    - `+/-`を押すとノードの開閉を変更します。
    - `➕`を押すと子ノードを全て展開し、`➖`を押すと全て閉じます
    - `＋`を押すと一段展開し、`－`を押すと一段閉じます。
- 雲の追加・削除
    - 雲ボタンを押すとノードに雲を追加します。
    - 点線の雲のボタンを押すとノードから雲を削除します。
- ノードの編集
    - 複数のノードを選択した後に`マージ(M)`を押すと、全てを欠小具して一つのノードにします。
    - 一つあるいは複数のノードを選択して分割の右横にある`。(L)`、`改行`、`空行(K)`を押すとそれぞれ。・改行・空行でノードを分割します。
    - 一つあるいは複数のノードを選択して削除の右横にある`改行(J)`、`空白`を押すとそれぞれ改行・空白を削除します。
    - 一つあるいは複数のノードを選択して改行の右横にある`。(C)`を押すと。の後を改行します。
- ノード並び順を変更
    - 複数のノードを選択して`逆順`を押すと逆の順番に並べ替えます。
    - 複数のノードを選択して`回転`を押すとノードがひとつ下に移動し、一番下のノードが先頭ノードになるよう並べ替えます。
- ノードを追加
    - `日付(U)`を押すと選択したノードの子ノードに日付(yyyy/MM/dd形式)を追加します。
    - `時間(I)`を押すと選択したノードの子ノードに時間(HH:mm:ss形式)を追加します。
- ノード書式をコピー
    - 書式をコピーしたいノードを選択した後に`書式`を押して、コピー元書式のノードを選択すると、書式（文字色、背景色）をコピーします。
- ショートカット
    - ボタンの括弧にある文字をAlt付きで押すとで機能を呼び出せます。
    - `Alt-/` ⇒ `+/-`ボタン
    - `Alt-Home` ⇒ `➕`ボタン
    - `Alt-End` ⇒ `➖`ボタン
    - `Alt-PageUp` ⇒ `＋`ボタン
    - `Alt-PageDown` ⇒ `－`ボタン
    - `Alt-.` ⇒ 雲を追加
    - `Alt-.` ⇒ 雲を削除

## 免責事項
このastah* pluginは無償で自由に利用することができます。
このastah* pluginを利用したことによる、astah*ファイルの破損により生じた生じたあらゆる損害等について一切責任を負いません。

以上
