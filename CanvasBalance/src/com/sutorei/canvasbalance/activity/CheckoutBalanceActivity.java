package com.sutorei.canvasbalance.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sutorei.canvasbalance.R;
import com.sutorei.canvasbalance.domain.TaskData;
import com.sutorei.canvasbalance.storage.PathConstants;
import com.sutorei.canvasbalance.view.BalanceView;

public class CheckoutBalanceActivity extends Activity {
	
	private TextView taskText, taskTitle;
	private ImageButton buttonBack, buttonCheck, buttonRestart;
	private int levelId, levelNumber;
	private SharedPreferences shpref;
	private SharedPreferences.Editor editor;
	private List<BalanceView> balanceViews;
	private LinearLayout balanceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_layout);
		taskText = (TextView)findViewById(R.id.task_description);
		taskTitle = (TextView)findViewById(R.id.task_title);
		buttonBack = (ImageButton)findViewById(R.id.image_button_back);
		buttonCheck = (ImageButton)findViewById(R.id.image_button_check);
		buttonRestart = (ImageButton)findViewById(R.id.image_button_restart);
		balanceList = (LinearLayout)findViewById(R.id.linear_balance_container);
		
		shpref = getSharedPreferences(PathConstants.SHARED_PROGRESS_NAME, 0);
		editor = shpref.edit();
		buttonBack.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		final TaskData taskData = null;
		
		taskTitle.setText("Lesson " + String.valueOf(levelNumber + 1));
		taskText.setText(taskData.getTaskText());
		
		balanceViews = new ArrayList<BalanceView>();
		for (int i = 0; i < taskData.getBalanceData().size(); ++i){
//			balanceViews.add(new BalanceView(this, taskData, i, null));
			balanceList.addView(balanceViews.get(i));
		}
		buttonCheck.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				boolean mistakePresent = false;
				for (int i = 0; i < balanceViews.size(); ++i){
					if(balanceViews.get(i).getCurrentState() !=
						taskData.getBalanceData().get(i).getBalanceState() ||
						!balanceViews.get(i).getAvaliableObjects().isEmpty()){
						mistakePresent = true;
						break;
					}
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutBalanceActivity.this);
				if (mistakePresent){
					builder.setIcon(R.drawable.icon_smilered);
					builder.setTitle("������ ������ � �������");
				    builder.setInverseBackgroundForced(true);
					builder.setPositiveButton("����������",new DialogInterface.OnClickListener()
				    {
				        @Override
				        public void onClick(DialogInterface dialog, int which) 
				        {
				            dialog.dismiss();
				        }
				    });
				
					editor.putInt(String.valueOf(levelId), -1);
				} else {
					builder.setIcon(R.drawable.icon_smilegreen);
				    builder.setTitle("������ ������ ���������");
				    builder.setInverseBackgroundForced(true);
					builder.setPositiveButton("������!",new DialogInterface.OnClickListener()
				    {
				        @Override
				        public void onClick(DialogInterface dialog, int which) 
				        {
				            dialog.dismiss();
				        }
				    });
					editor.putInt(String.valueOf(levelId), 1);
				}
				AlertDialog alert = builder.create();
				alert.show();
				editor.commit();
			}
		});

		
		buttonRestart.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.checkout_balance, menu);
		return true;
	}
	
	
/*	private class BalanceAdapter extends ArrayAdapter<BalanceView>{
		private ArrayList<BalanceView> balanceViews;
		
		public BalanceAdapter(Context context, int textViewResourceId, BalanceView[] items) {
	        super(context, textViewResourceId, items);
	        this.balanceViews = new ArrayList<BalanceView>(Arrays.asList(items));
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        BalanceView bv = ba;
	        if (it != null) {
	            ImageView iv = (ImageView) v.findViewById(R.id.list_item_image);
	            if (iv != null) {
	                iv.setImageDrawable(it.getImage());
	            }
	        }

	        return v;
	    }
		
	} */

}
