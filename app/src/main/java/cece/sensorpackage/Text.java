/*
package cece.sensorpackage;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;


public class Text extends Activity implements OnClickListener{

	private Button startBtn;
	private Button stopBtn;
	private TextView result;
	private Button saoyisao;
	private RecordingThread recordingThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		startBtn = (Button) findViewById(R.id.start_btn);
		startBtn.setOnClickListener(this);

		stopBtn = (Button) findViewById(R.id.stop_btn);
		stopBtn.setOnClickListener(this);

		saoyisao = findViewById(R.id.saoyisao);
		saoyisao.setOnClickListener(this);

		result = findViewById(R.id.jieguo);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.start_btn:
			
			startBtn.setVisibility(View.INVISIBLE);
			stopBtn.setVisibility(View.VISIBLE);

			recordingThread = new RecordingThread();
			recordingThread.start();
			
			break;

		case R.id.stop_btn:
			
			startBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.INVISIBLE);
			
			recordingThread.stopRecording();
			
			break;
			case R.id.saoyisao:

				Intent intent = new Intent(Text.this, CaptureActivity.class);
				*/
/*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
				 * 也可以不传这个参数
				 * 不传的话  默认都为默认不震动  其他都为true
				 * *//*


				//ZxingConfig config = new ZxingConfig();
				//config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
				//config.setPlayBeep(true);//是否播放提示音
				//config.setShake(true);//是否震动
				//config.setShowAlbum(true);//是否显示相册
				//config.setShowFlashLight(true);//是否显示闪光灯
				//intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
				startActivityForResult(intent, 0);
				break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				String content = data.getStringExtra(Constant.CODED_CONTENT);
				result.setText("扫描结果为：" + content);
			}
		}
	}


}
*/
