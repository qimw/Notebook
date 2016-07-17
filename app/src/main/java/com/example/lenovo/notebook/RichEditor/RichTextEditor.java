package com.example.lenovo.notebook.RichEditor;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.lenovo.notebook.R;
import com.example.lenovo.notebook.global.NotebookApp;

/**
 * 这是一个富文本编辑器，给外部提供insertImage接口，添加的图片跟当前光标所在位置有关
 * 
 * @author xmuSistone
 * 
 */
@SuppressLint({ "NewApi", "InflateParams" })
public class RichTextEditor extends ScrollView {
	private static final int EDIT_PADDING = 10; // edittext常规padding是10dp
	private static final int EDIT_FIRST_PADDING_TOP = 10; // 第一个EditText的paddingTop值

	private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
	private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
	private LayoutInflater inflater;
	private OnKeyListener keyListener; // 所有EditText的软键盘监听器
	private OnClickListener btnListener; // 图片右上角红叉按钮监听器
	private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
	private EditText lastFocusEdit = null; // 最近被聚焦的EditText
	private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
	private int editNormalPadding = 0; //
	private int disappearingImageIndex = 0;
	private EditText thisFocusEdit = null;
	//以下是新增富文本部分
	private Boolean color = false;
	private Boolean italic = false;
	private Boolean bold = false;
	private Boolean underline = false;
	private MyTextWatcher textWatcher = new MyTextWatcher();

	public void setColor(){
		if(color == false){
			color =true;
		}else{
			color=false;
		}
	}

	public void setBold() {
		if(bold == false){
			bold = true;
		}else{
			bold = false;
		}
	}

	public void setItalic() {
		if(italic == false){
			italic = true;
		}else{
			italic = false;
		}
	}

	public void setUnderline() {
		if(underline == false){
			underline = true;
		}else{
			underline = false;
		}
	}

	private AppCompatActivity appCompatActivity = null;//用来初始化图片

	public RichTextEditor(Context context) {
		this(context, null);
	}

	public RichTextEditor(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflater = LayoutInflater.from(context);

		// 1. 初始化allLayout
		allLayout = new LinearLayout(context);
		allLayout.setOrientation(LinearLayout.VERTICAL);
		allLayout.setBackgroundColor(Color.WHITE);
		setupLayoutTransitions();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(allLayout, layoutParams);

		// 2. 初始化键盘退格监听
		// 主要用来处理点击回删按钮时，view的一些列合并操作
		keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
					EditText edit = (EditText) v;
					onBackspacePress(edit);
				}
				return false;
			}
		};

		// 3. 图片叉掉处理
		btnListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout parentView = (RelativeLayout) v.getParent();
				onImageCloseClick(parentView);
			}
		};

		focusListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lastFocusEdit = (EditText) v;
				}
			}
		};
	}

	public void createFirstEditTest(){

		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		editNormalPadding = dip2px(EDIT_PADDING);
		EditText firstEdit = createEditText("input here",
				dip2px(EDIT_FIRST_PADDING_TOP));
		allLayout.addView(firstEdit, firstEditParam);
		lastFocusEdit = firstEdit;
	}

	public EditText createEditText(Spanned text) {
		if(lastFocusEdit == null){
			LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			EditText editText = createEditText(null,dip2px(EDIT_PADDING));
			allLayout.addView(editText, firstEditParam);
			lastFocusEdit = editText;
			Log.d("holo","from null");
			if(text != null){
				editText.setText(text);
				editText.setSelection(text.length());
			}
			lastFocusEdit.requestFocus();
			return editText;
		}else if (lastFocusEdit.getText().toString().equals("")){
			Log.d("holo","from  ");
			lastFocusEdit.setText(text);
			lastFocusEdit.setSelection(text.length());
			return lastFocusEdit;
		}else{
			Log.d("holo","from not null");
			LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			EditText editText = createEditText(null,dip2px(EDIT_PADDING));
			allLayout.addView(editText, firstEditParam);
			lastFocusEdit = editText;
			editText.setText(text);
			editText.setSelection(text.length());
			lastFocusEdit.requestFocus();
			return editText;
		}
	}

	/**
	 * 处理软键盘backSpace回退事件
	 *
	 * @param editTxt
	 *            光标所在的文本输入框
	 */
	private void onBackspacePress(EditText editTxt) {
		int startSelection = editTxt.getSelectionStart();
		// 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
		if (startSelection == 0) {
			int editIndex = allLayout.indexOfChild(editTxt);
			View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
																// 则返回的是null
			if (null != preView) {
				if (preView instanceof RelativeLayout) {
					// 光标EditText的上一个view对应的是图片
					onImageCloseClick(preView);
				} else if (preView instanceof EditText) {
					// 光标EditText的上一个view对应的还是文本框EditText
					String str1 = editTxt.getText().toString();
					EditText preEdit = (EditText) preView;
					String str2 = preEdit.getText().toString();

					// 合并文本view时，不需要transition动画
					allLayout.setLayoutTransition(null);
					allLayout.removeView(editTxt);
					allLayout.setLayoutTransition(mTransitioner); // 恢复transition动画

					// 文本合并
					preEdit.setText(str2 + str1);
					preEdit.requestFocus();
					preEdit.setSelection(str2.length(), str2.length());
					lastFocusEdit = preEdit;
				}
			}
		}
	}

	/**
	 * 处理图片叉掉的点击事件
	 *
	 * @param view
	 *            整个image对应的relativeLayout view
	 * @type 删除类型 0代表backspace删除 1代表按红叉按钮删除
	 */
	private void onImageCloseClick(View view) {
		if (!mTransitioner.isRunning()) {
			disappearingImageIndex = allLayout.indexOfChild(view);
			allLayout.removeView(view);
		}
	}

	/**
	 * 生成文本输入框
	 */
	public EditText createEditText(String hint, int paddingTop) {
		EditText editText = (EditText) inflater.inflate(R.layout.edit_item1,
				null);

		editText.setOnKeyListener(keyListener);
		editText.setTag(viewTagIndex++);
		editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, 0);
		editText.setHint(hint);
		editText.setOnFocusChangeListener(focusListener);
		editText.addTextChangedListener(new MyTextWatcher());
		return editText;
	}

	/**
	 * 生成图片View
	 */
	private RelativeLayout createImageLayout() {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.edit_imageview, null);
		layout.setTag(viewTagIndex++);
		View closeView = layout.findViewById(R.id.image_close);
		closeView.setTag(layout.getTag());
		closeView.setOnClickListener(btnListener);
		return layout;
	}

	/**
	 * 根据绝对路径添加view
	 *
	 * @param imagePath
	 */
	public void insertImage(String imagePath, AppCompatActivity appCompatActivity) {
		this.appCompatActivity = appCompatActivity;
		Bitmap bmp = getScaledBitmap(imagePath, appCompatActivity.getWindowManager().getDefaultDisplay().getWidth());
		insertImage(bmp, imagePath);
	}

	/**
	 * 插入一张图片
	 */
	private void insertImage(Bitmap bitmap, String imagePath) {
		if(lastFocusEdit == null){
			createEditText(null);
		}
			String lastEditStr = lastFocusEdit.getText().toString();
			int cursorIndex = lastFocusEdit.getSelectionStart();

			String editStr1 = lastEditStr.substring(0, cursorIndex);
			int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);
			if (lastEditStr.length() == 0 || editStr1.length() == 0) {
				// 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
				addImageViewAtIndex(lastEditIndex, bitmap, imagePath);
			} else {
				// 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
				lastFocusEdit.setSelection(editStr1.length());
				addEditTextAtIndex(lastEditIndex + 1);
				addImageViewAtIndex(lastEditIndex +1, bitmap, imagePath);
				//lastFocusEdit.setSelection(editStr1.length(), editStr1.length());
			}
		hideKeyBoard();
	}

	/**
	 * 隐藏小键盘
	 */
	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
	}

	/**
	 * 在特定位置插入EditText
	 *
	 * @param index
	 *            位置
	 *
	 *            EditText显示的文字
	 */
	private void addEditTextAtIndex(final int index) {
		EditText editText2 = createEditText(null,dip2px(EDIT_PADDING));
		editText2.requestFocus();
		lastFocusEdit = editText2;
//		// 请注意此处，EditText添加、或删除不触动Transition动画
		allLayout.setLayoutTransition(null);
		allLayout.addView(editText2,index);

		allLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
	}

	/**
	 * 在特定位置添加ImageView
	 */
	private void addImageViewAtIndex(final int index, Bitmap bmp,
			String imagePath) {
		final RelativeLayout imageLayout = createImageLayout();
		DataImageView imageView = (DataImageView) imageLayout
				.findViewById(R.id.edit_imageView);
		imageView.setImageBitmap(bmp);
		imageView.setBitmap(bmp);
		imageView.setAbsolutePath(imagePath);

		// 调整imageView的高度
		int imageHeight = appCompatActivity.getWindowManager().getDefaultDisplay().getWidth() * bmp.getHeight() / bmp.getWidth();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, imageHeight);
		imageView.setLayoutParams(lp);
		allLayout.addView(imageLayout, index);
		// onActivityResult无法触发动画，此处post处理
//		allLayout.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//
//			}
//		}, 200);
	}

	/**
	 * 根据view的宽度，动态缩放bitmap尺寸
	 *
	 * @param width
	 *            view的宽度
	 */
	public static Bitmap getScaledBitmap(String filePath, int width) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int sampleSize = options.outWidth > width ? options.outWidth / width
				+ 1 : 1;
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 初始化transition动画
	 */
	private void setupLayoutTransitions() {
		mTransitioner = new LayoutTransition();
		allLayout.setLayoutTransition(mTransitioner);
		mTransitioner.addTransitionListener(new TransitionListener() {

			@Override
			public void startTransition(LayoutTransition transition,
					ViewGroup container, View view, int transitionType) {
			}

			@Override
			public void endTransition(LayoutTransition transition,
					ViewGroup container, View view, int transitionType) {
				if (!transition.isRunning()
						&& transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
					// transition动画结束，合并EditText
					 //mergeEditText();
				}
			}
		});
		mTransitioner.setDuration(300);
	}

	/**
	 * 图片删除的时候，如果上下方都是EditText，则合并处理
	 */
	private void mergeEditText() {
		View preView = allLayout.getChildAt(disappearingImageIndex - 1);
		View nextView = allLayout.getChildAt(disappearingImageIndex);
		if (preView != null && preView instanceof EditText && null != nextView
				&& nextView instanceof EditText) {
			Log.d("LeiTest", "合并EditText");
			EditText preEdit = (EditText) preView;
			EditText nextEdit = (EditText) nextView;
			Spanned str1 = preEdit.getText();
			Spanned str2 = nextEdit.getText();
//			Spanned mergeText = "";
//			if (str2.length() > 0) {
//				mergeText = str1 + "\n" + str2;
//			} else {
//				mergeText = str1;
//			}

//			allLayout.setLayoutTransition(null);
//			allLayout.removeView(nextEdit);
//			preEdit.setText(mergeText);
//			preEdit.requestFocus();
//			preEdit.setSelection(str1.length(), str1.length());
//			allLayout.setLayoutTransition(mTransitioner);
		}
	}

	/**
	 * dp和pixel转换
	 *
	 * @param dipValue
	 *            dp值
	 * @return 像素值
	 */
	public int dip2px(float dipValue) {
		float m = getContext().getResources().getDisplayMetrics().density;
		return (int) (dipValue * m + 0.5f);
	}

	/**
	 * 对外提供的接口, 生成编辑数据上传
	 */
	public List<EditData> buildEditData() {
		List<EditData> dataList = new ArrayList<EditData>();

		//添加其余项
		int num = allLayout.getChildCount();
		for (int index = 0; index < num; index++) {
			View itemView = allLayout.getChildAt(index);
			EditData itemData = new EditData();
			if (itemView instanceof EditText) {
				EditText item = (EditText) itemView;
				itemData.inputStr = Html.toHtml(item.getText());
			} else if (itemView instanceof RelativeLayout) {
				DataImageView item = (DataImageView) itemView
						.findViewById(R.id.edit_imageView);
				itemData.imagePath = item.getAbsolutePath();
				itemData.bitmap = item.getBitmap();
			}
			dataList.add(itemData);
		}

		return dataList;
	}

	public class EditData {
		public String inputStr;
		public String imagePath;
		public Bitmap bitmap;
	}

	public static SpannableStringBuilder trimTrailingWhitespace(
			SpannableStringBuilder spannableString) {

		if (spannableString == null)
			return new SpannableStringBuilder("");

		int i = spannableString.length();

		// loop back to the first non-whitespace character
		while (--i >= 0 && Character.isWhitespace(spannableString.charAt(i))) {
		}

		return new SpannableStringBuilder(spannableString.subSequence(0, i + 1));
	}
	class MyTextWatcher implements TextWatcher {
		boolean yes = false;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			Log.d("holo","from before " + s + start + count + after);
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d("holo","ontextchange");
			SpannableString spannableString = new SpannableString(s);
			if (yes == false) {
				Log.d("holo","ontextchange");
				yes = true;
				if (color == true) {
					ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
					spannableString.setSpan(span, start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (italic == true) {
					spannableString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (bold == true) {
					spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (underline == true) {
					spannableString.setSpan(new UnderlineSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				lastFocusEdit.setText(spannableString);
				if(count - before > 0){
					lastFocusEdit.setSelection(start + count - before);
				}else{
					lastFocusEdit.setSelection(start);
				}


				Log.d("holo",Html.toHtml(spannableString));

			}
			else {
				yes = false;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			Log.d("holo","from after" + s );
		}
	}

}