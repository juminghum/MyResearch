FusedDotProduct:
	単精度浮動小数点数(32bit)の融合積和演算器のプログラム

tile:
	FusedDotProductをRoCCインターフェースを介して接続させた部分。
	LazyRoCC.scalaのみ、元のプログラムと変更点あり。(最初からLazyRoCC.scalaだけpushすればよかった。。。)
	
Configs.scala:
	chipyard/generators/rocket-chip/src/main/scala/subsystem にあるRocketを生成する際のコンフィグのリスト。アクセラレータを接続させるために加筆した
	
bootloader_test:
	verilatorで生成したRISC-Vシミュレーターを用いてCプログラム実行させたフォルダ。
	main.cを参照

how2sim.txt:
	chipyardでのverilatorを用いたシミュレーションのやり方のテキスト。


僕が設計したプログラムを動かしたい場合
	
	1. chipyardをcloneする
	
	2. FusedDotProductフォルダを chipyard/generators/rocket-chip/src/main/scala に入れる
	
	3. chipyard/generators/rocket-chip/src/main/scala/tile の中のLazyRoCC.scalaを書き換えたものに替える
	
	4. chipyard/generators/rocket-chip/src/main/scala/subsystemの中のConfigs.scalaを書き換えたものに替える
	
	5. bootloader_testを適当なところに作成する
	
	6. how2sim.txtにしたがってプログラムを実行
	
