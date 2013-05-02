/**
 * ACadEngineActivity.java
 * Main Activity for the aCAD application.  Based on AndEngine.
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 * MODIFIED BY TEAM CPU AT MIDWESTERN STATE UNIVERSITY
 *   - Junior Fletcher
 *   - Veronica McClure
 *   - Lauren Rios
 *   - Chase Sawyer
 *   - Matt Swezey
 */

package socs.acad;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Vector;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.shader.PositionColorShaderProgram;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.color.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import socs.acad.MutablePolygon;
import socs.acad.MutableEllipseArc;
import socs.acad.MutablePolygon.PolygonState;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(16)
public class ACadEngineActivity extends BaseGameActivity implements IOnSceneTouchListener, IPinchZoomDetectorListener
{
	// Variables related to visual display
	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	final private int MENU_WIDTH = 200;
	static final float MENU_WIDTH_RATIO = 0.15f;
	private static BaseGameActivity instance = null;

	// Layout related variables
	public FrameLayout frameLayout;
	public ScrollView scrollView;
	public LinearLayout linearLayout;
	private static MapScale mapScale;
	public RelativeLayout menuLayout;
	LinearLayout tabHolder;

	// AndEngine Variables
	private Camera camera;
	private Scene splashScene;
	private static ACadScene mainScene;
	private HUD hud; // Heads Up Display (AndEngine concept)
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;
	private float lastX, lastY;

	// Display Layers used within the scene
	private GroupEntity worldLayer, hardscapeLayer, landscapeLayer, sprinklerLayer, pipeLayer;
	private Grid gridLayer;

	// Tabbed Menu
	private String[] menuItems = new String[] { "Hardscape", "Landscape", "Sprinkler", "Pipe", "Menu" };
	private ImageView[] tabImage;
	private int tabWidth, tabHeight;

	// Splash Screen variables
	private BitmapTextureAtlas splashTextureAtlas;
	private ITextureRegion splashTextureRegion;
	private Sprite splash;

	// View Buffer Object variable, OpenGL related
	private VertexBufferObjectManager vboManager;

	// Texture Variables
	public BitmapTextureAtlas mBitmapTextureAtlas;
	public ITextureRegion mNormalTextureRegion;
	public ITextureRegion mPushedTextureRegion;
	public ButtonSprite mImageButton;

	// File saving / JSON related
	private String getfileName = null;
	float jsontouchY, jsontouchX, jsonpolyAngle;
	float[] jsonXvertex = new float[100];
	float[] jsonYvertex = new float[100];
	private double jsonEllipseRadius;
	private double jsonEllipseAngle;
	private Shape jsons;

	// Font used for measurements
	static public Font measurementFont;

	// Variables set by Dialogs
	private float dialogWidth;
	private float dialogHeight;

	// Colors based upon Android's color palette
	// http://developer.android.com/design/style/color.html
	static final int menuDarkGray = 0xFF666666;
	static final int menuYellow = 0xFFFFBB33;
	static final int menuGreen = 0xFF99CC00;
	static final int menuBlue = 0xFF3399FF;
	static final int menuPurple = 0xFF9933FF;
	static final int menuRed = 0xFFFF4444;
	static final int menuWhite = 0xFFFFFFFF;
	protected static final PolygonState VIEW = null;
	protected static final PolygonState EDIT = null;
	static int gridLineColorScheme = 0;

	// State Variables
	private boolean isZooming = false;
	private boolean isDragging = false;
	static private boolean disallowChildControl = false;

	// ENUM for dealing with multiple scenes
	private enum SceneType
	{
		SPLASH, MAIN
	}

	private SceneType currentScene = SceneType.SPLASH;

	// ENUM for tracking which button was recently pressed
	public static enum ButtonActive
	{
		NONE, HOUSE, FENCE, DRIVEWAY, PATIO, POOL, SIDEWALK, TREE, SHRUB, FLOWERBED, SPRINKLER, ROTORHEAD, PIPEONEHALF, PIPEONE, PIPEONEANDONEHALF, PIPEONEONEANDONEQUARTER, PIPETHREEQUARTER, PIPETWO, SPRINKLERREMOVE
	}

	private static ButtonActive buttonActive = ButtonActive.NONE;
	public static int currentColor = 0;

	/**
	 * AndEngine method called when Engine is being instantiated
	 */
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		// Save a reference to self for later
		instance = this;

		// Retrieve the native width and height for use with the camera
		CAMERA_WIDTH = instance.getResources().getDisplayMetrics().widthPixels;
		CAMERA_HEIGHT = instance.getResources().getDisplayMetrics().heightPixels;
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		// Lock orientation to Landscape, but flip to avoid upside down
		// operation
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
		return engineOptions;
	}

	/**
	 * AndEngine method called when resources are created
	 */
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		// Load the Splash Screen texture
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 720, 397, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, this, "splash.png", 0, 0);
		splashTextureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	/**
	 * AndEngine method called when the screen is created.
	 */
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		initSplashScene();
		pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);
		this.mPinchZoomDetector = new PinchZoomDetector(this);
	}

	/**
	 * AndEngine method called when the Scene is to be populated.
	 */
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		// Register a TimerHandler to hide the Splash Screen after everything
		// else is loaded and to attach the HUD.
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);

				// Load Things that take time
				loadResources();
				loadScenes();

				// Show the splash screen a little longer
				try
				{
					Thread.sleep(300);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				// Hide the splash screen and show the main Scene
				splash.detachSelf();
				mEngine.setScene(mainScene);
				currentScene = SceneType.MAIN;

				// Draw the menu and attach the Map Scale.
				// NOTE: Attaching the Map Scale cannot be done on this
				// thread, so we push that action to the UI Thread.
				drawMenu();
				instance.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hud.attachChild(mapScale);
					}
				});
			}
		}));

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	/**
	 * Method for handling KeyDown events
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (currentScene)
			{
				case SPLASH:
					break;
				case MAIN:
					System.exit(0);
					break;
			}
		}
		return false;
	}

	/**
	 * Load resources
	 */
	public void loadResources()
	{
		// Load the font used for measurements
		measurementFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 18,
				Color.WHITE_ABGR_PACKED_INT);
		measurementFont.load();

		// Create the HUD
		hud = new HUD();
	}

	/**
	 * Load the Scenes
	 */
	private void loadScenes()
	{
		// Load main scene
		mainScene = new ACadScene(CAMERA_WIDTH, CAMERA_HEIGHT, MENU_WIDTH);
		mainScene.setBackground(new Background(.15f, .16f, .55f));

		mainScene.setOnAreaTouchTraversalFrontToBack();

		// The vbo is a high-speed cache to handle vertices storage that OpenGL
		// uses to draw the triangles that constitute polygons. We are saving
		// this reference so that it can be used as we add objects to the Scene.
		vboManager = this.getVertexBufferObjectManager();
		mainScene.setTouchAreaBindingOnActionDownEnabled(true);
		mainScene.setOnSceneTouchListener(this);

		// Attach the layers
		gridLayer = new Grid(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, PositionColorShaderProgram.getInstance(), vboManager);
		worldLayer = new GroupEntity();
		hardscapeLayer = new GroupEntity();
		landscapeLayer = new GroupEntity();
		sprinklerLayer = new GroupEntity();
		pipeLayer = new GroupEntity();

		// The mapScale variable must be created before gridLayer is attached
		mapScale = new MapScale(230, CAMERA_HEIGHT - 70, 24 / 2, 24, vboManager);

		// Attach the layers to the Scene
		mainScene.attachChild(gridLayer);
		mainScene.attachChild(worldLayer);
		mainScene.attachChild(hardscapeLayer);
		mainScene.attachChild(landscapeLayer);
		mainScene.attachChild(sprinklerLayer);
		mainScene.attachChild(pipeLayer);
	}

	/**
	 * Create the tabbed menu
	 */
	private void drawMenu()
	{
		// Set the HUD
		this.camera.setHUD(hud);

		// Dimension the imageView array
		tabImage = new ImageView[menuItems.length];

		// Set height of tab to screen eight divided by amount of tabs needed
		tabHeight = (int) (frameLayout.getHeight() / (menuItems.length));

		// Set tab width for tablet screen
		tabWidth = (int) (tabHeight / 3.5);

		// Set parameters for all children of the parent menuLayout
		scrollView = new ScrollView(this);
		scrollView.setLayoutParams(new ViewGroup.LayoutParams(menuLayout.getWidth() - tabWidth, menuLayout.getHeight()));
		scrollView.setBackgroundColor(menuYellow);

		// Create the Linear Layouts
		linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		tabHolder = new LinearLayout(this);
		tabHolder.setOrientation(LinearLayout.VERTICAL);
		final RelativeLayout.LayoutParams tabLayoutParams = new RelativeLayout.LayoutParams(tabWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
		tabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		// Generate imageViews for each menu tab
		for (int i = 0; i < menuItems.length; i++)
		{
			final int iTab = i;
			tabImage[i] = new ImageView(this);
			tabImage[i].setOnClickListener(new ImageView.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					drawSubMenu(iTab);
					makeTabs(iTab);
				}
			});
		}

		// Populate the menu
		drawSubMenu(0);
		makeTabs(0);

		// Need to create new runnable for addition to frameLayout
		// Because it is called inside ITimerCallback, otherwise application
		// will stop
		instance.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				menuLayout.addView(scrollView);
				scrollView.addView(linearLayout);
				menuLayout.addView(tabHolder, tabLayoutParams);
				for (int i = 0; i < menuItems.length; i++)
				{
					tabHolder.addView(tabImage[i]);
				}
				tabImage[4].performClick();
			}
		});
	}

	/**
	 * Method to generate tab bitmaps and add them to their respective
	 * ImageViews
	 */
	private void makeTabs(int activeTab)
	{
		float tabAngleRatio = (float) 0.4;
		int textWidth, textHeight;
		int textMaxSize = 40;

		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		Rect textBounds = new Rect();
		textPaint.setColor(menuWhite);
		boolean found = true;

		// Shrink textMaxSize
		do
		{
			found = true;
			textMaxSize--;
			textPaint.setTextSize(textMaxSize);

			for (int i = 0; i < menuItems.length; i++)
			{
				textPaint.getTextBounds(menuItems[i], 0, menuItems[i].length(), textBounds);
				if (textBounds.width() > ((tabHeight - (tabWidth * tabAngleRatio)) - (tabWidth * tabAngleRatio)))
				{
					found = false;
				}
			}

		} while (!found);

		// Cycle through and paint all tabs
		for (int i = 0; i < menuItems.length; i++)
		{
			Bitmap tabBitmap = Bitmap.createBitmap(tabWidth, tabHeight, Bitmap.Config.ARGB_8888);
			Canvas tabCanvas = new Canvas(tabBitmap);
			Path tabPath = new Path();
			Path textPath = new Path();
			Paint tabPaint = new Paint();
			tabPaint.setAntiAlias(true);

			// Choose the color for the tab
			switch (i)
			{
				case 0:
					tabPaint.setColor(menuYellow);
					break;
				case 1:
					tabPaint.setColor(menuGreen);
					break;
				case 2:
					tabPaint.setColor(menuBlue);
					break;
				case 3:
					tabPaint.setColor(menuPurple);
					break;
				case 4:
					tabPaint.setColor(menuRed);
					break;
				default:
					tabPaint.setColor(menuWhite);
					break;
			}

			// Get the dimensions of the text
			textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			textPaint.getTextBounds(menuItems[i], 0, menuItems[i].length(), textBounds);
			textWidth = textBounds.width();
			textPaint.getTextBounds("e", 0, 1, textBounds);
			textHeight = textBounds.height();

			// Create the text path
			textPath.moveTo((float) ((tabWidth - textHeight) / 2), (float) ((tabHeight - textWidth) / 2));
			textPath.lineTo((float) ((tabWidth - textHeight) / 2), (float) (tabHeight - (tabHeight - (textWidth * (float) 1.1)) / 2));

			// Create the tab path
			tabPath.moveTo(0, 0);
			tabPath.lineTo((float) (tabWidth), (float) (tabWidth * tabAngleRatio));
			tabPath.lineTo((float) (tabWidth), (float) (tabHeight - (tabWidth * tabAngleRatio)));
			tabPath.lineTo((float) 0, (float) tabHeight);

			// Draw the tab and text
			tabCanvas.drawARGB(0, 0, 0, 0);
			tabCanvas.drawPath(tabPath, tabPaint);
			tabCanvas.drawTextOnPath(menuItems[i], textPath, 0, 0, textPaint);
			tabCanvas.save();

			tabImage[i].setImageBitmap(tabBitmap);
		}
	}

	/**
	 * Create a menu button with the appropriate information. Used to populate
	 * the tab menu buttons and reduce duplicated code.
	 */
	public Button createMenuButton(View.OnClickListener pListener, LinearLayout.LayoutParams pLayoutParams, int pImageID, CharSequence pText)
	{
		Button button = new Button(this);

		// Set button characteristics
		Drawable pImage = getResources().getDrawable(pImageID);
		button.setText(pText);
		button.setTextColor(menuDarkGray);
		button.setGravity(Gravity.CENTER_HORIZONTAL);
		button.setLayoutParams(pLayoutParams);
		button.setCompoundDrawablesWithIntrinsicBounds(null, pImage, null, null);
		button.setBackgroundResource(R.drawable.menu_button_background);
		button.setOnClickListener(pListener);

		return button;
	}

	/**
	 * Method to generate buttons based on which menu is selected
	 */
	private void drawSubMenu(int menu)
	{
		// Clear the layout
		linearLayout.removeAllViews();
		Button button;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) ((menuLayout.getWidth() - tabWidth) * (float) 0.9), LayoutParams.WRAP_CONTENT);
		lp.setMargins(5, 5, 5, 0);

		switch (menu)
		{
			case 0:
				// Hardscape tab
				setLayerTouchable(hardscapeLayer);
				buttonActive = ButtonActive.NONE;

				// Set scrollView background equal to Android Blue
				scrollView.setBackgroundColor(menuYellow);

				// Create first "house" button
				button = createMenuButton(menuHouseListener, lp, R.drawable.ic_menu_home, "House");
				linearLayout.addView(button);

				// Create second "fence" button
				button = createMenuButton(menuFenceListener, lp, R.drawable.ic_picket_fence, "Fence");
				linearLayout.addView(button);

				// Create third "driveway" button
				button = createMenuButton(menuDrivewayListener, lp, R.drawable.ic_menu_driveway, "Driveway");
				linearLayout.addView(button);

				// Create fourth "patio" button
				button = createMenuButton(menuPatioListener, lp, R.drawable.ic_menu_patio, "Patio");
				linearLayout.addView(button);

				// Create fifth "pool" button
				button = createMenuButton(menuPoolListener, lp, R.drawable.ic_menu_pool, "Pool");
				linearLayout.addView(button);

				// Create sixth "sidewalk" button
				button = createMenuButton(menuSidewalkListener, lp, R.drawable.ic_menu_sidewalk, "Sidewalk");
				linearLayout.addView(button);

				// Create seventh "delete" button
				button = createMenuButton(Remove, lp, R.drawable.delete, "Delete");
				linearLayout.addView(button);
				break;
			case 1:
				// Landscape tab
				setLayerTouchable(landscapeLayer);
				buttonActive = ButtonActive.NONE;
				scrollView.setBackgroundColor(menuGreen);

				// Create first "tree" button
				button = createMenuButton(menuTreeListener, lp, R.drawable.ic_menu_tree, "Tree");
				linearLayout.addView(button);

				// Create second "shrub" button
				button = createMenuButton(menuShrubListener, lp, R.drawable.ic_menu_shrubs, "Shrub");
				linearLayout.addView(button);

				// Create third "flowerbed" button
				button = createMenuButton(menuFlowerbedListener, lp, R.drawable.ic_menu_flower_bed, "Flowerbed");
				linearLayout.addView(button);

				// Create fourth "delete" button
				button = createMenuButton(Remove, lp, R.drawable.delete, "Delete");
				linearLayout.addView(button);
				break;
			case 2:
				// Sprinkler tab
				setLayerTouchable(sprinklerLayer);
				buttonActive = ButtonActive.NONE;
				scrollView.setBackgroundColor(menuBlue);

				// Create Sprinkler button
				button = createMenuButton(menuSprinklerListener, lp, R.drawable.ic_menu_sprinkler, "Sprinkler");
				linearLayout.addView(button);

				// Create Rotorhead button
				button = createMenuButton(menuRotorheadListener, lp, R.drawable.ic_menu_sprinkler_head, "Rotorhead");
				linearLayout.addView(button);

				// Create "delete" button
				button = createMenuButton(Remove, lp, R.drawable.delete, "Delete");
				linearLayout.addView(button);
				break;
			case 3:
				// Pipe tab
				setLayerTouchable(pipeLayer);
				buttonActive = ButtonActive.NONE;
				scrollView.setBackgroundColor(menuPurple);

				// Create first pipe button button
				button = createMenuButton(menuPipeOneHalfListener, lp, R.drawable.ic_pipe_one_half, "Pipe");
				linearLayout.addView(button);

				// Create 2nd pipe button button
				button = createMenuButton(menuPipeThreeQuarterListener, lp, R.drawable.ic_pipe_three_quarter, "Pipe");
				linearLayout.addView(button);

				// Create 3rd pipe button button
				button = createMenuButton(menuPipeOneListener, lp, R.drawable.ic_pipe_one, "Pipe");
				linearLayout.addView(button);

				// Create 4th pipe button button
				button = createMenuButton(menuPipeOneAndOneHalfListener, lp, R.drawable.ic_pipe_one_and_one_half, "Pipe");
				linearLayout.addView(button);

				// Create 5th pipe button button
				button = createMenuButton(menuPipeTwoListener, lp, R.drawable.ic_pipe_two, "Pipe");
				linearLayout.addView(button);

				// Create "delete" button
				button = createMenuButton(Remove, lp, R.drawable.delete, "Delete");
				linearLayout.addView(button);
				break;
			case 4:
				// Menu tab
				setLayerTouchable(worldLayer);
				buttonActive = ButtonActive.NONE;
				scrollView.setBackgroundColor(menuRed);

				// Create first "new" button
				button = createMenuButton(menuNewListener, lp, R.drawable.ic_menu_new, "New");
				linearLayout.addView(button);

				// Create second "open file..." button
				button = createMenuButton(menuOpenListener, lp, R.drawable.ic_menu_open, "Open");
				linearLayout.addView(button);

				// Create third "Save" button
				button = createMenuButton(menuSaveListener, lp, R.drawable.ic_menu_save, "Save");
				linearLayout.addView(button);

				// Create fourth "Save As" button
				button = createMenuButton(menuSaveAsListener, lp, R.drawable.ic_menu_save_as, "Save As");
				linearLayout.addView(button);
				
				// Create "Delete File" button
				button = createMenuButton(menuDeleteFileListener, lp, R.drawable.delete, "Delete" + "\n" + "File");
				linearLayout.addView(button);

				// Create export to png button
				button = createMenuButton(menuExportToPng, lp, R.drawable.ic_menu_settings, "Export");
				linearLayout.addView(button);

				// Create color scheme button
				button = createMenuButton(colorschemelistener, lp, R.drawable.ic_menu_settings, "Color Scheme");
				linearLayout.addView(button);

				// Create sixth "re-center" button
				button = createMenuButton(menuReCenterListener, lp, R.drawable.ic_menu_new, "Re-Center");
				linearLayout.addView(button);

				// Create "Exit" button
				button = createMenuButton(menuExitListener, lp, R.drawable.ic_menu_exit, "Exit");
				linearLayout.addView(button);

				break;
			default:
				break;
		}
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	}

	/**
	 * Override onSetContentView() so that we can use our own layout manager and
	 * populate it as needed.
	 */
	@Override
	protected void onSetContentView()
	{
		// Use a Frame Layout so that the tab menu can be overlayed on grid
		frameLayout = new FrameLayout(this);
		final FrameLayout.LayoutParams frameLayoutLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		final int menuWidth = (int) (instance.getResources().getDisplayMetrics().widthPixels * MENU_WIDTH_RATIO);

		// Create the menu layout
		menuLayout = new RelativeLayout(this);
		final RelativeLayout.LayoutParams menuLayoutParams = new RelativeLayout.LayoutParams(menuWidth, RelativeLayout.LayoutParams.MATCH_PARENT);

		// Create the Render Surface View for AndEngine to use
		this.mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(mEngine, this);
		final FrameLayout.LayoutParams surfaceViewLayoutParams = new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());
		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);

		// Add the Views and populate them appropriately
		scrollView = new ScrollView(this);
		scrollView.setLayoutParams(new ViewGroup.LayoutParams(MENU_WIDTH, CAMERA_HEIGHT));
		frameLayout.addView(scrollView);
		linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		frameLayout.addView(menuLayout, menuLayoutParams);

		// Finally, set the content view to frameLayout
		this.setContentView(frameLayout, frameLayoutLayoutParams);
	}

	/**
	 * Initialize the Splash Screen scene
	 */
	private void initSplashScene()
	{
		splashScene = new Scene();
		splash = new Sprite(0, 0, splashTextureRegion, mEngine.getVertexBufferObjectManager())
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		splash.setScale(1f);
		splash.setPosition((CAMERA_WIDTH - splash.getWidth()) * 0.5f, (CAMERA_HEIGHT - splash.getHeight()) * 0.5f);
		splashScene.attachChild(splash);
	}

	/**
	 * Main Scene Area Touch Listener
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		// Early bailout check if user is pinch zooming
		if (mPinchZoomDetector.isZooming())
		{
			isZooming = true;
			disallowChildControl = true;
			return true;
		}
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
		{
			if (isZooming || isDragging)
			{
				// Finished zooming or dragging.
				// Clear the state and exit.
				resetState();
				return true;
			}
			// Set all MutablePolygons to View Mode
			boolean wasEditing = false;

			Vector<MutablePolygon> toChange = new Vector<MutablePolygon>();
			int totalChildren = pScene.getChildCount();
			IEntity e;
			for (int i = 0; i < totalChildren; i++)
			{
				e = pScene.getChildByIndex(i);
				if (e instanceof MutablePolygon)
				{
					if (((MutablePolygon) e).getState() != PolygonState.VIEW)
					{
						// We cannot change the state inside this loop,
						// because it will change the the child count
						// and order. Instead, add them to a vector to be
						// removed next.
						toChange.add((MutablePolygon) e);
					}
				}
			}
			// Clear the state for any MutablePolygons
			for (int i = 0; i < toChange.size(); i++)
			{
				toChange.get(i).setState(PolygonState.VIEW);
				wasEditing = true;
			}

			// If we were not editing anything, then add another MutablePolygon
			if (!wasEditing)
			{
				// Get local coordinates
				final float[] touchAreaLocalCoordinates = pScene.convertSceneToLocalCoordinates(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				final float localX = touchAreaLocalCoordinates[Constants.VERTEX_INDEX_X];
				final float localY = touchAreaLocalCoordinates[Constants.VERTEX_INDEX_Y];
				switch (buttonActive)
				{
					case NONE:
						// No Action
						break;
					case PATIO:
						currentColor = 0;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialogGetDimensions(new Runnable()
								{
									@Override
									public void run()
									{
										// This code is run after the OK button
										// is
										// clicked
										float halfWidth = dialogWidth / 2;
										float halfHeight = dialogHeight / 2;
										// Add the object to the correct layer
										MutablePolygon s = new MutablePolygon(localX, localY, vboManager);
										s.setFont(measurementFont);
										s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight,
												halfHeight, halfHeight });
										hardscapeLayer.attachChild(s);
									}
								});
							}
						});
						break;
					case HOUSE:
						currentColor = 0;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialogGetDimensions(new Runnable()
								{
									@Override
									public void run()
									{
										// This code is run after the OK button
										// is
										// clicked
										float halfWidth = dialogWidth / 2;
										float halfHeight = dialogHeight / 2;
										// Add the object to the correct layer
										MutablePolygon s = new MutablePolygon(localX, localY, vboManager);
										s.setFont(measurementFont);
										s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight,
												halfHeight, halfHeight });
										hardscapeLayer.attachChild(s);
									}
								});
							}
						});
						break;
					case POOL:
						currentColor = 0;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialogGetDimensions(new Runnable()
								{
									@Override
									public void run()
									{
										// This code is run after the OK button
										// is
										// clicked
										float halfWidth = dialogWidth / 2;
										float halfHeight = dialogHeight / 2;
										// Add the object to the correct layer
										MutablePolygon s = new MutablePolygon(localX, localY, vboManager);
										s.setFont(measurementFont);
										s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight,
												halfHeight, halfHeight });
										hardscapeLayer.attachChild(s);
									}
								});
							}
						});
						break;
					case FENCE:
						currentColor = 0;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								// Add the object to the correct layer
								MutablePolygon s = new MutableLine(localX, localY, vboManager);
								s.setFont(measurementFont);
								s.updateVertices(new float[] { -60, 60 }, new float[] { 0, 0 });
								hardscapeLayer.attachChild(s);
							}
						});
						break;
					case DRIVEWAY:
						currentColor = 0;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialogGetDimensions(new Runnable()
								{
									@Override
									public void run()
									{
										// This code is run after the OK button

										float halfWidth = dialogWidth / 2;
										float halfHeight = dialogHeight / 2;
										// Add the object to the correct layer
										MutablePolygon s = new MutablePolygon(localX, localY, vboManager);
										s.setFont(measurementFont);
										s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight,
												halfHeight, halfHeight });
										hardscapeLayer.attachChild(s);
									}
								});
							}
						});
						break;
					case SIDEWALK:
						currentColor = 0;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialogGetDimensions(new Runnable()
								{
									@Override
									public void run()
									{
										// This code is run after the OK button
										// is
										// clicked
										float halfWidth = dialogWidth / 2;
										float halfHeight = dialogHeight / 2;
										// Add the object to the correct layer
										MutablePolygon s = new MutableRectangle(localX, localY, vboManager);
										s.setFont(measurementFont);
										s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight,
												halfHeight, halfHeight });
										hardscapeLayer.attachChild(s);
									}
								});
							}
						});
						break;
					case TREE:
						currentColor = 1;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								// Add the object to the correct layer
								MutablePolygon s = new MutableEllipseArc(localX, localY, vboManager);
								s.setFont(measurementFont);
								switch (ACadEngineActivity.currentColor)
								{
									case 0:
										s.setColor(1f, .76f, .28f, .5f);
										break;
									case 1:
										s.setColor(.64f, .82f, .1f, .5f);
										break;
									case 2:
										s.setColor(.28f, .64f, 1f, .5f);
										break;
									case 3:
										s.setColor(.64f, .28f, 1f, .5f);
										break;
									default:
										s.setColor(Color.WHITE);
										break;
								}
								landscapeLayer.attachChild(s);
							}
						});
						break;
					case SHRUB:
						currentColor = 1;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								// Add the object to the correct layer
								MutablePolygon s = new MutableEllipseArc(localX, localY, vboManager);
								s.setFont(measurementFont);
								switch (ACadEngineActivity.currentColor)
								{
									case 0:
										s.setColor(1f, .76f, .28f, .5f);
										break;
									case 1:
										s.setColor(.64f, .82f, .1f, .5f);
										break;
									case 2:
										s.setColor(.28f, .64f, 1f, .5f);
										break;
									case 3:
										s.setColor(.64f, .28f, 1f, .5f);
										break;
									default:
										s.setColor(Color.WHITE);
										break;
								}
								landscapeLayer.attachChild(s);
							}
						});
						break;
					case FLOWERBED:
						currentColor = 1;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialogGetDimensions(new Runnable()
								{
									@Override
									public void run()
									{
										// This code is run after the OK button
										// is
										// clicked
										float halfWidth = dialogWidth / 2;
										float halfHeight = dialogHeight / 2;
										// Add the object to the correct layer
										MutablePolygon s = new MutablePolygon(localX, localY, vboManager);
										s.setFont(measurementFont);
										s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight,
												halfHeight, halfHeight });
										landscapeLayer.attachChild(s);
									}
								});
							}
						});
						break;
					case SPRINKLER:
						currentColor = 2;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								// Add the object to the correct layer
								MutablePolygon s = new MutableEllipseArc(localX, localY, vboManager);
								s.setFont(measurementFont);
								sprinklerLayer.attachChild(s);
							}
						});
						break;
					case ROTORHEAD:
						currentColor = 2;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								// Add the object to the correct layer
								MutablePolygon s = new MutableEllipseArc(localX, localY, vboManager);
								s.setFont(measurementFont);
								sprinklerLayer.attachChild(s);
							}
						});
						break;
					default: // else, then assume a pipe
						currentColor = 3;
						this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								// Add the object to the correct layer
								MutablePolygon s = new MutableLine(localX, localY, vboManager);
								s.setFont(measurementFont);
								s.updateVertices(new float[] { -60, 60 }, new float[] { 0, 0 });
								pipeLayer.attachChild(s);
							}
						});
						break;
				}
				// Clear the active button indicator
				buttonActive = ButtonActive.NONE;
			}
			return true;
		}
		// If the user is just now touching the screen, save the initial
		// coordinates
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
		{
			lastX = pSceneTouchEvent.getX();
			lastY = pSceneTouchEvent.getY();
			return true;
		}
		// If the user is dragging, then use the previous coordinates to
		// determine
		// where everything should be moved.
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE)
		{
			float currentX = pSceneTouchEvent.getX();
			float currentY = pSceneTouchEvent.getY();
			// Don't start moving unless it is more than 5 pixels
			if (isDragging || (Math.abs(lastX - currentX) > 5) || (Math.abs(lastY - currentY) > 5))
			{
				isDragging = true;
				disallowChildControl = true;
				pScene.setPosition(pScene.getX() - (lastX - currentX), pScene.getY() - (lastY - currentY));
				lastX = pSceneTouchEvent.getX();
				lastY = pSceneTouchEvent.getY();
				return true;
			}
		}
		return false;
	}

	/**
	 * Reset all state variables.
	 */
	public void resetState()
	{
		isDragging = false;
		isZooming = false;
		disallowChildControl = false;
	}

	/**
	 * Set layer touchables
	 */
	public void setLayerTouchable(GroupEntity group)
	{
		GroupEntity groups[] = new GroupEntity[] { worldLayer, hardscapeLayer, landscapeLayer, sprinklerLayer, pipeLayer };
		IEntity child;
		for (int i = 0; i < groups.length; i++)
		{
			for (int j = 0; j < groups[i].getChildCount(); j++)
			{
				child = groups[i].getChildByIndex(j);
				if (child instanceof MutablePolygon)
				{
					if (groups[i] == group)
					{
						// This layer is touchable
						((MutablePolygon) child).setTouchableState(true);
						//resets current color when tab is selected
						resetCurrentColor(i);
					} else
					{
						// This layer is not touchable
						if (((MutablePolygon) child).getState() == MutablePolygon.PolygonState.EDIT)
						{
							((MutablePolygon) child).setState(MutablePolygon.PolygonState.VIEW);
						}
						((MutablePolygon) child).setTouchableState(false);
					}
				}
			}
		}
	}

	/**
	 * Static Method for children to use to verify that they can respond to
	 * touches on the screen.
	 */
	static public boolean disallowChildControl()
	{
		return disallowChildControl;
	}

	/**
	 * Method called when Pinch Zoom starts
	 */
	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent)
	{
		this.mPinchZoomStartedCameraZoomFactor = mainScene.getScaleX();
	}

	/**
	 * Method called when Pinch Zoom is happening
	 */
	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor)
	{
		mainScene.setScale(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
	}

	/**
	 * Method called when Pinch Zoom is finished
	 */
	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor)
	{
		mainScene.setScale(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
	}

	/**
	 * Return the MapScale object, accessible from any method.
	 */
	public static MapScale getMapScale()
	{
		return mapScale;
	}

	/**
	 * Method called when wanting to create a new layout
	 */
	private void newFile()
	{
		dialogGetDimensions(new Runnable()
		{
			@Override
			public void run()
			{
				// This code is run after the OK button is clicked
				float halfWidth = dialogWidth / 2;
				float halfHeight = dialogHeight / 2;
				// Add the object to the correct layer
				MutablePolygon s = new MutablePolygon(0, 0, vboManager);
				((MutablePolygon) s).setFont(measurementFont);
				s.updateVertices(new float[] { -halfWidth, halfWidth, halfWidth, -halfWidth }, new float[] { -halfHeight, -halfHeight, halfHeight, halfHeight });
				worldLayer.attachChild(s);
				getfileName = null;

				// The reset cannot be run on this thread.
				instance.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						// Center the scene
						mainScene.resetView();
						mainScene.activeEntity = null;
					}
				});
			}
		});

	}

	/**
	 * Method to display an alert box
	 */
	private void displaySaveAlert()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Do you want to save the current design?");

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				saveDialog(1); // 1 for deleting the children off the screen
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				deleteChildren();
				newFile();
			}
		});
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.cancel();
			}
		});
		builder.show();
	}

	/**
	 * Method called to read JSON Polygon object
	 */
	public void readPolygonObj(JsonReader reader, String layerName)
	{
		try
		{
			reader.beginObject();
			while (reader.hasNext())
			{
				String type = reader.nextName();
				String polygonType = getPolygonType(type);

				if (polygonType.equals("MutableEllipseArc"))
				{
					readEllipseJson(reader, layerName);

				} else if (polygonType.equals("MutableLine"))
				{
					readMutablePolygonJson(reader, layerName);
					jsons = new MutableLine((float) jsontouchX, (float) jsontouchY, vboManager);
					((MutablePolygon) jsons).setFont(measurementFont);
					((MutablePolygon) jsons).updateVertices(jsonXvertex, jsonYvertex);
					getLayerEntity(layerName).attachChild(jsons);
				} else
				{
					readMutablePolygonJson(reader, layerName);
					jsons = new MutableRectangle((float) jsontouchX, (float) jsontouchY, vboManager);
					((MutablePolygon) jsons).setFont(measurementFont);
					((MutablePolygon) jsons).updateVertices(jsonXvertex, jsonYvertex);
					getLayerEntity(layerName).attachChild(jsons);
				}
			}
			reader.endObject();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Method to return the Polygon type
	 */
	private String getPolygonType(String type)
	{
		if (type.equalsIgnoreCase("class socs.acad.MutablePolygon"))
		{
			return "MutablePolygon";
		} else if (type.equalsIgnoreCase("class socs.acad.MutableRectangle"))
		{
			return "MutableRectangle";
		} else if (type.equalsIgnoreCase("class socs.acad.MutableLine"))
		{
			return "MutableLine";
		} else if (type.equalsIgnoreCase("class socs.acad.MutableEllipseArc"))
		{
			return "MutableEllipse";
		} else
		{
			return "";
		}
	}

	/**
	 * Method to read the Polygon information
	 */
	private void readMutablePolygonJson(JsonReader reader, String layerName)
	{
		try
		{
			// Cycle through reader array
			reader.beginArray();
			while (reader.hasNext())
			{
				reader.beginObject();
				while (reader.hasNext())
				{
					String name = reader.nextName();
					// Determine what we are trying to read
					if (name.equals("PolygonTouchX"))
					{
						jsontouchX = (float) reader.nextDouble();
					} else if (name.equals("PolygonTouchY"))
					{
						jsontouchY = (float) reader.nextDouble();
					} else if (name.equals("PolygonAngle"))
					{
						jsonpolyAngle = (float) reader.nextDouble();
					} else if (name.equals("PolygonX"))
					{
						reader.beginArray();
						int j = 0;
						while (reader.hasNext())
						{
							jsonXvertex[j] = (float) (reader.nextDouble());
							j++;
						}
						reader.endArray();
					} else if (name.equals("PolygonY"))
					{
						reader.beginArray();
						int k = 0;
						while (reader.hasNext())
						{
							jsonYvertex[k] = (float) (reader.nextDouble());
							k++;
						}
						reader.endArray();
					}
				}
				reader.endObject();
			}
			reader.endArray();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Method for reading Ellipse JSON
	 */
	public void readEllipseJson(JsonReader reader, String layerName)
	{

		try
		{
			// Cycle through the reader array
			reader.beginArray();
			while (reader.hasNext())
			{
				reader.beginObject();
				while (reader.hasNext())
				{
					String name = reader.nextName();
					if (name.equals("TouchX"))
					{
						jsontouchX = (float) reader.nextDouble();
					} else if (name.equals("TouchY"))
					{
						jsontouchY = (float) reader.nextDouble();
					} else if (name.equals("EllipseAngle"))
					{
						jsonEllipseAngle = reader.nextDouble();
					} else if (name.equals("EllipseRadius"))
					{
						jsonEllipseRadius = reader.nextDouble();
					}
				}
				reader.endObject();
			}
			reader.endArray();
			jsons = new MutableEllipseArc(jsontouchX, jsontouchY, vboManager);
			((MutablePolygon) jsons).setJSONEllipseVertices(jsonEllipseRadius, jsonEllipseAngle);
			((MutablePolygon) jsons).setFont(measurementFont);
			getLayerEntity(layerName).attachChild(jsons);

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Return the layer associated with a layer name
	 */
	public GroupEntity getLayerEntity(String name)
	{
		if (name.equals("hardscapeLayer"))
			return hardscapeLayer;
		else if (name.equals("landscapeLayer"))
			return landscapeLayer;
		else if (name.equals("sprinklerLayer"))
			return sprinklerLayer;
		else
			return pipeLayer;
	}

	/**
	 * Method to display the save dialog
	 */
	public void saveDialog(final int Inputfrom)
	{
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.file_name_entry, null);
		final EditText nameEntry = (EditText) textEntryView.findViewById(R.id.editText1);

		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

		alertbox.setTitle("Enter file name");
		alertbox.setView(textEntryView);
		alertbox.setNegativeButton("Cancel", null);

		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{

				/* User clicked OK so do some stuff */
				dialog.cancel();
				getfileName = nameEntry.getText().toString();
				saveSceneIterator(getfileName);
				if (Inputfrom == 1)
				{
					deleteChildren();
					// inputPlotDetails();
				}
			}
		});
		alertbox.create();
		// dialogIsDisplayed = true;
		alertbox.show();
	}

	/**
	 * Method to display the delete dialog
	 */
	public void deleteDialog()
	{
		// pulls in data from blueprint and screenshot directory
		final File myblueprintdir = new File(Environment.getExternalStorageDirectory(), "/aCAD/blueprints/");
		final File myscreenshotdir = new File(Environment.getExternalStorageDirectory(), "/aCAD/screenshots/");

		//populates each list
		final String List1[] = myblueprintdir.list();
		final String List2[] = myscreenshotdir.list();
		
		//combines both arrays into one to display blueprints and files  in one list
		final String[] array1and2 = new String[List1.length + List2.length];
		for (int i = 0; i < List1.length; i++)
		{
			array1and2[i] = List1[i];
		}

		int j = 0;
		for (int i = List1.length; i < List1.length + List2.length; i++)
		{
			array1and2[i] = List2[j];
			j++;
		}

		//if there are files, display the list
		if (array1and2.length > 0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select a file to delete");
		builder.setNegativeButton("Cancel", null);

		builder.setItems(array1and2, new DialogInterface.OnClickListener()
		{

			//runs when file is clicked
			@Override
			public void onClick(DialogInterface dialog, int selection)
			{
				File file = null;
				dialog.cancel();
					try
					{
					if(Arrays.asList(List1).contains(array1and2[selection]))
						file = new File(Environment.getExternalStorageDirectory(), "/aCAD/blueprints/" + array1and2[selection]);
					else
						file = new File(Environment.getExternalStorageDirectory(), "/aCAD/screenshots/" + array1and2[selection]);
					
					file.delete();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		//show dialog
		builder.create();
		builder.show();
		} else
		{//if no files exist, show toast msg
			Toast.makeText(ACadEngineActivity.this, "No files were found!", Toast.LENGTH_SHORT).show();
		}
	}
 
	/**
	 * Method to display the open dialog
	 */
	public void openDialog(final int Inputfrom)
	{
		File mydir = new File(Environment.getExternalStorageDirectory(), "/aCAD/blueprints/");
		final String List[] = mydir.list();

		if (List.length > 0)
		{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a file to open");
		builder.setItems(List, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int selection)
			{
				getfileName = List[selection];
				dialog.cancel();
				deleteChildren();

				try
				{
					File file = new File(Environment.getExternalStorageDirectory(), "/aCAD/blueprints/" + List[selection]);

					FileInputStream fIn = new FileInputStream(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(fIn));
					StringBuilder textInFile = new StringBuilder();
					String line;

					while ((line = br.readLine()) != null)
					{
						textInFile.append(line);
						textInFile.append("\n");
					}

					br.close();

					String text = textInFile.toString();
					InputStream is = new ByteArrayInputStream(text.getBytes());
					JsonReader reader = new JsonReader(new InputStreamReader(is));

					reader.beginArray();
					reader.beginObject();
					while (reader.hasNext())
					{
						String layerName = reader.nextName();

						reader.beginArray();
						while (reader.hasNext())
						{
							readPolygonObj(reader, layerName);
						}
						reader.endArray();
					}
					reader.endObject();
					reader.endArray();
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		builder.setNegativeButton("Cancel", null);
		builder.create();
		builder.show();
		} else
		{
			Toast.makeText(ACadEngineActivity.this, "No files were found!", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Show the Exit dialog
	 */
	public void showExitDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?").setCancelable(false).setTitle("EXIT")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						finish();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				});

		builder.create(); // This takes the AlertDialog and prepares it
		builder.show(); // shows the alert dialog

	}

	/**
	 * Save elements in the layout to a file
	 */
	public void saveSceneIterator(String fileName)
	{
		String toastMsg = "";
		File mydir = new File(Environment.getExternalStorageDirectory() + "/aCAD/blueprints/");

		if (!mydir.exists())
		{
			mydir.mkdirs();
		}
		final File file = new File(mydir, fileName);

		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			} else
			{
				new File(mydir, fileName).delete();
				file.createNewFile();
			}
		} catch (IOException e)
		{
			toastMsg = e.toString();
			e.printStackTrace();
		}

		FileWriter saveFilewriter = null;

		try
		{
			saveFilewriter = new FileWriter(file, true);
			saveFilewriter.append("[");
		} catch (IOException e)
		{
			toastMsg = e.toString();
			e.printStackTrace();
		}

		for (int i = 1; i < mainScene.getChildCount(); i++)
		{
			IEntity childOfScene = mainScene.getChildByIndex(i);
			if (childOfScene.getChildCount() != 0)
			{
				JSONObject json = new JSONObject(); // object for layer name
				JSONArray newPolygon = new JSONArray(); // To get a list of
														// polygons
				JSONArray polygonArray = null;
				JSONObject polygonName = null;

				for (int j = 0; j < childOfScene.getChildCount(); j++)
				{
					polygonArray = new JSONArray();
					polygonName = new JSONObject(); // appending the type of
													// polygon
					// collection of polygon attributes
					IEntity childOfLayer = childOfScene.getChildByIndex(j);
					if (childOfLayer instanceof MutablePolygon)
					{
						try
						{
							polygonArray.put(((MutablePolygon) childOfLayer).getJSONVertices());
							polygonName.put(childOfLayer.getClass().toString(), polygonArray);
							newPolygon.put(polygonName);
						} catch (JSONException e)
						{
							toastMsg = e.toString();
							e.printStackTrace();
						}
					}
				}
				try
				{
					json.put(getLayer(i), newPolygon);
					saveFilewriter.append(json.toString());
				} catch (JSONException e)
				{
					toastMsg = e.toString();
					e.printStackTrace();
				} catch (IOException e)
				{
					toastMsg = e.toString();
					e.printStackTrace();
				}
			}
		}
		try
		{
			saveFilewriter.append("]");
			saveFilewriter.flush();
			saveFilewriter.close();

			toastMsg = "File Saved";
		} catch (IOException e)
		{
			toastMsg = e.toString();
			e.printStackTrace();
		}

		Toast.makeText(ACadEngineActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Clear the screen
	 */
	public void deleteChildren()
	{
		// Exclude the grid layer, it should stay
		for (int i = 1; i < mainScene.getChildCount(); i++)
		{
			IEntity layer = mainScene.getChildByIndex(i);
			for (int j = 0; j < layer.getChildCount(); j++)
			{
				layer.detachChild(layer);
				layer.detachChildren();
			}
		}

	}

	/**
	 * Standard, reusable dialog to get dimensions
	 */
	public void dialogGetDimensions(final Runnable runnable)
	{
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Dimensions");

		final TextView lengthText = new TextView(this);
		final EditText lengthValue = new EditText(this);
		lengthValue.setInputType(InputType.TYPE_CLASS_NUMBER);
		final TextView widthText = new TextView(this);
		final EditText widthValue = new EditText(this);
		widthValue.setInputType(InputType.TYPE_CLASS_NUMBER);

		lengthText.setText("Length in Feet");
		widthText.setText("Width in Feet");

		layout.addView(lengthText);
		layout.addView(lengthValue);
		layout.addView(widthText);
		layout.addView(widthValue);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String lengthText = lengthValue.getText().toString();
				String widthText = widthValue.getText().toString();

				if (!lengthText.equals("") && !widthText.equals(""))
				{
					dialogHeight = 12 * Float.parseFloat(lengthText);
					dialogWidth = 12 * Float.parseFloat(widthText);
					// Run the code that accompanies this instance
					runnable.run();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.cancel();
			}
		});

		alert.setView(layout);
		alert.show();
	}

	/**
	 * Get the layer
	 */
	public String getLayer(int item)
	{
		switch (item)
		{
			case 1:
				return ("worldLayer");
			case 2:
				return ("hardscapeLayer");
			case 3:
				return ("landscapeLayer");
			case 4:
				return ("sprinklerLayer");
			case 5:
				return ("pipeLayer");
			default:
				return null;
		}
	}

	/**
	 * Menu Button OnClickListeners
	 */

	/**
	 * House Button Listener
	 */
	View.OnClickListener menuHouseListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View linearLayout)
		{
			buttonActive = ButtonActive.HOUSE;
		}
	};

	/**
	 * Export to Png Listener
	 */
	View.OnClickListener menuExportToPng = new View.OnClickListener()
	{//exports screenshot as png
		@Override
		public void onClick(View scrollView)
		{
			File mydir = new File(Environment.getExternalStorageDirectory() + "/aCAD/screenshots/");

			if (!mydir.exists())
			{//creates directory if it doesn't exist
				mydir.mkdirs();
			}

			//appends timestamp to filename for png
			java.util.Date date = new java.util.Date();
			String filename = "";
			String temp = getfileName + "@";

			
			if (!temp.startsWith("null"))
				filename = getfileName + " ";

			//replaces :'s with .'s and spaces with underscores
			String s = new Timestamp(date.getTime()).toString();
			s = s.replace(":", ".").substring(0, s.length() - 4);
			s = s.replace(" ", "_").substring(0, s.length() - 4);
			filename = filename + s;

			//appends extension
			if (!filename.contains(".png"))
				filename = filename + ".png";

			//file save toast msg with directory
			String msg = "Filed Saved: /ACaD/screenshots/" + filename;

			 //this method requires root access Process sh = null; try { sh =
			 Process sh = null;
			 try
			 {
			 sh = Runtime.getRuntime().exec("su", null, null);
			 } catch (IOException e1)
			 {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 }
			 OutputStream os = sh.getOutputStream();
			 try
			 {
			 os.write(("/system/bin/screencap -p " +
			 "/sdcard/" +"root" + filename ).getBytes("ASCII"));
			 try{ Thread.sleep(3000); }catch(InterruptedException e){ }
			 Toast.makeText(ACadEngineActivity.this, msg,
			 Toast.LENGTH_SHORT).show();
			 } catch (UnsupportedEncodingException e1)
			 {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 } catch (IOException e1)
			 {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 }
			 try
			 {
			 os.flush();
			 } catch (IOException e1)
			 {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 }
			 try
			 {
			 os.close();
			 } catch (IOException e1)
			 {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 }
			 try
			 {
			 sh.waitFor();
			 } catch (InterruptedException e1)
			 {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 }

			 //this method is partially functional, glitched
			View view = getWindow().getDecorView().findViewById(android.R.id.content);
			view.setDrawingCacheEnabled(true);
			Bitmap screenshot = view.getDrawingCache(false);

			try
			{
				File f = new File(Environment.getExternalStorageDirectory(), "/aCAD/screenshots/" + filename);
				f.createNewFile();
				OutputStream outStream = new FileOutputStream(f);
				screenshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				outStream.close();
				Toast.makeText(ACadEngineActivity.this, msg, Toast.LENGTH_SHORT).show();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			view.setDrawingCacheEnabled(false);
		}

	};

	/**
	 * Fence Button Listener
	 */
	View.OnClickListener menuFenceListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View linearLayout)
		{
			buttonActive = ButtonActive.FENCE;
		}
	};

	/**
	 * Driveway Button Listener
	 */
	View.OnClickListener menuDrivewayListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.DRIVEWAY;
		}
	};

	/**
	 * Patio Button Listener
	 */
	View.OnClickListener menuPatioListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.PATIO;
		}
	};

	/**
	 * Pool Button Listener
	 */
	View.OnClickListener menuPoolListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.POOL;
		}
	};

	/**
	 * Sidewalk Button Listener
	 */
	View.OnClickListener menuSidewalkListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.SIDEWALK;
		}
	};

	/**
	 * Tree Button Listener
	 */
	View.OnClickListener menuTreeListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.TREE;
		}
	};

	/**
	 * Shrub Button Listener
	 */
	View.OnClickListener menuShrubListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.SHRUB;
		}
	};

	/**
	 * Flowerbed Button Listener
	 */
	View.OnClickListener menuFlowerbedListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.FLOWERBED;
		}
	};

	/**
	 * Sprinkler Button Listener
	 */
	View.OnClickListener menuSprinklerListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.SPRINKLER;
		}
	};

	/**
	 * Rotorhead Button Listener
	 */
	View.OnClickListener menuRotorheadListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.ROTORHEAD;
		}
	};

	/**
	 * 1/2 Pipe Button Listener
	 */
	View.OnClickListener menuPipeOneHalfListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.PIPEONEHALF;
		}
	};

	/**
	 * 3/4 Pipe Button Listener
	 */
	View.OnClickListener menuPipeThreeQuarterListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.PIPETHREEQUARTER;
		}
	};

	/**
	 * 1 Pipe Button Listener
	 */
	View.OnClickListener menuPipeOneListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.PIPEONE;
		}
	};

	/**
	 * 1 1/2 Pipe Button Listener
	 */
	View.OnClickListener menuPipeOneAndOneHalfListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.PIPEONEANDONEHALF;
		}
	};
	/**
	 * 2 Pipe Button Listener
	 */
	View.OnClickListener menuPipeTwoListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			buttonActive = ButtonActive.PIPETWO;
		}
	};

	/**
	 * New Button Listener
	 */
	View.OnClickListener menuNewListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			if (pipeLayer.getChildCount() != 0 || sprinklerLayer.getChildCount() != 0 || hardscapeLayer.getChildCount() != 0
					|| landscapeLayer.getChildCount() != 0)
			{
				displaySaveAlert();
			} else
			{
				newFile();
			}
		}
	};

	/**
	 * Open Button Listener
	 */
	View.OnClickListener menuOpenListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			openDialog(0); // 0 for performing no action on the children
		}
	};

	/**
	 * Save Button Listener
	 */
	View.OnClickListener menuSaveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			if (getfileName == null)
			{
				saveDialog(0); // 0 for performing no action on the children
			} else
			{
				saveSceneIterator(getfileName);
			}
		}
	};

	/**
	 * Save As Button Listener
	 */
	View.OnClickListener menuSaveAsListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			saveDialog(0); // 0 for performing no action on the children
		}
	};

	/**
	 * Exit Button Listener
	 */
	View.OnClickListener menuExitListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			showExitDialog();
		}
	};

	/**
	 * Delete Button Listener
	 */
	View.OnClickListener menuDeleteFileListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			deleteDialog();
		}
	};

	/**
	 * Re-Center Button Listener
	 */
	View.OnClickListener menuReCenterListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			// The reset cannot be run on this thread.
			instance.runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{
					mainScene.resetView();
					mainScene.activeEntity = null;
				}
			});
		}
	};

	/**
	 * remove objects listener
	 */
	View.OnClickListener Remove = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			for (int i = 1; i < mainScene.getChildCount(); i++)
			{
				IEntity childOfScene = mainScene.getChildByIndex(i);
				if (childOfScene.getChildCount() != 0)
				{
					for (int j = 0; j < childOfScene.getChildCount(); j++)
					{
						// collection of polygon attributes
						final IEntity childOfLayer = childOfScene.getChildByIndex(j);
						if (childOfLayer instanceof MutablePolygon)
						{
							if (((MutablePolygon) childOfLayer).polygonState.name().equalsIgnoreCase("Edit"))
								instance.runOnUpdateThread(new Runnable()
								{
									@Override
									public void run()
									{//toggles visibility of object
										((MutablePolygon) childOfLayer).setChildrenVisible(false);
									}
								});
						}
					}
				}
			}
		}
	};

	/**
	 * ColorScheme Listener
	 */
	View.OnClickListener colorschemelistener = new View.OnClickListener()
	{
		@Override
		public void onClick(View scrollView)
		{
			if (gridLineColorScheme == 0)
			{
				gridLineColorScheme = 1;
				mainScene.setBackground(new Background(.82f, .82f, .82f));
				for (int i = 1; i < mainScene.getChildCount(); i++)
				{
					IEntity childOfScene = mainScene.getChildByIndex(i);
					if (childOfScene.getChildCount() != 0)
					{

						for (int j = 0; j < childOfScene.getChildCount(); j++)
						{
							// collection of polygon attributes
							IEntity childOfLayer = childOfScene.getChildByIndex(j);
							if (childOfLayer instanceof MutablePolygon)
							{//sets outlines to black
								((MutablePolygon) childOfLayer).populateMeasurements();
								((MutablePolygon) childOfLayer).populateMeasurements();
								((MutablePolygon) childOfLayer).outline.setColor(Color.BLACK);
								((MutablePolygon) childOfLayer).regenerateOutline();
							}
						}
					}
				}
			} else
			{//sets it back to normal
				gridLineColorScheme = 0;
				mainScene.setBackground(new Background(.15f, .16f, .55f));
				for (int i = 1; i < mainScene.getChildCount(); i++)
				{
					IEntity childOfScene = mainScene.getChildByIndex(i);
					if (childOfScene.getChildCount() != 0)
					{
						for (int j = 0; j < childOfScene.getChildCount(); j++)
						{
							// collection of polygon attributes
							IEntity childOfLayer = childOfScene.getChildByIndex(j);
						}
					}
				}
			}
			instance.runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{
					mainScene.resetView();
				}
			});
		}
	};

	//resets current shape color
	public static void resetCurrentColor(int groupID)
	{
		if (gridLineColorScheme == 0)
			currentColor = groupID - 1;
	}
}