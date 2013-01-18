package merg.landscapesoftware;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;


public class Settings extends Activity
{
	public Paint paintLine; // used to draw lines onto bitmap
	 private Context context;
	
	 //enumeration type for different drawing option
	public enum drawOption
	{
		curve, line,  sprinkler
	};
	 //enumeration type for different drawing action
	public enum drawAction
	{
		draw, select, move, rotate, delete, showLen, none
	};
	 //enumeration type for different line style
	public enum lineStyle
	{
		solid_line, dashed_line, dotted, fine_dotted
	};
	 //enumeration type for different length units
	public enum LengthType
    {
        
        MilliMeter, Inch , Foot , Meter, Kilometer, Mile 
    }
	 //enumeration type for different velocity units
	public enum VelocityType
    {
       
        FeetPerSec,
        
        MeterPerSec 
    }
	
	 //enumeration type for different pressure units
	 public enum PressureType
	    {
	        
	        PoundPerSquareInchAbsolute,
	        
	        BarAbsolute ,
	        
	        KilopascalsAbsolute,
	        
	        PascalsAbsolute ,
	        
	        KilogramsPerSquareCentimeterAbsolute ,
	        
	        MegapascalsAbsolute ,
	        
	       
	        FeetHeadAbsolute,
	        
	        TorrAbsolute ,
	        
	        MilimeterOfMercuryAbsolute ,
	        
	        PoundPerSquareInchGauge ,
	        
	        BarGauge,
	        
	        KilopascalsGauge ,
	        
	        PascalsGauge ,
	        
	        KilogramsPerSquareCentimeterGauge,
	        
	        MegapascalsGauge,
	        
	        MilimeterOfMercuryGauge,
	        
	        MilliMetersOfWaterColumnGauge
	    }
	
	 int scale = 100; // default scale in meter
	 drawOption dOption; 
	 drawAction dAction;
	 int optionSelected;
	 boolean snaptolines;
	 EditText input;
	 lineStyle linestyle;
	 LengthType lengthunit = LengthType.Meter;
	 PressureType pressureunit;
	 VelocityType velocityunit;
	 int minA, maxA, minR, maxR;
	 TextView angView ;
	 TextView cdegreeView;
	 TextView radView ;
	 double Radius;
	 int Angle;
	 boolean displayPageDesc;
	 float curveDegree = 36;
	 boolean showGridLines = true;
	
	public Settings(Context context)
	{

		this.context = context;
		dOption = drawOption.line;
		dAction = drawAction.draw;
		
	      // set the initial display settings for the painted line
	      paintLine = new Paint();
	      paintLine.setAntiAlias(true); // smooth edges of drawn line
	      paintLine.setColor(Color.BLACK); // default color is black
	      paintLine.setStyle(Paint.Style.STROKE); // solid line
	      paintLine.setStrokeWidth(5); // set the default line width
	      paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
	      paintLine.setPathEffect(new DashPathEffect(new float[] {0,0}, 0)); // default path effect to draw solid line
	      displayPageDesc = false;
	      Radius = 0;
		  Angle = 0;
		  
		  linestyle = lineStyle.solid_line;
		
	      
	}

	 public void setDrawingColor(int color) 
	   {
	      paintLine.setColor(color);
	   } // end method setDrawingColor

	   // return the painted line's color
	   public int getDrawingColor() 
	   {
	      return paintLine.getColor();
	   } // end method getDrawingColor

	   // set the painted line's width
	   public void setLineWidth(int width) 
	   {
	      paintLine.setStrokeWidth(width);
	   } // end method setLineWidth

	   // return the painted line's width
	   public int getLineWidth() 
	   {
	      return (int) paintLine.getStrokeWidth();
	   } // end method getLineWidth
	   
	   public void setSprinklerRadius(double rad)
	   {  
		   Radius = rad;
	   }
	   
	   public void setCurvature(int curve) 
	   {
		   curveDegree = curve;
	   } 

	   public float getCurvature() 
	   {
	      return curveDegree;
	   } 
	   
	   
	   
	
	
	// variable to display various custom dialog
		private Dialog currentDialog;
	
	// display a dialog for selecting color
		public void showColorDialog(Dialog cDialog) 
		{
			currentDialog = cDialog;

			// get the color SeekBars and set their onChange listeners
			final SeekBar alphaSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.alphaSeekBar);
			final SeekBar redSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.redSeekBar);
			final SeekBar greenSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.greenSeekBar);
			final SeekBar blueSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.blueSeekBar);

			// register SeekBar event listeners
			alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
			redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
			greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
			blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

			// use current drawing color to set SeekBar values
			final int color = getDrawingColor();
			alphaSeekBar.setProgress(Color.alpha(color));
			redSeekBar.setProgress(Color.red(color));
			greenSeekBar.setProgress(Color.green(color));
			blueSeekBar.setProgress(Color.blue(color));

			// set the Set Color Button's onClickListener
			Button setColorButton = (Button) currentDialog
					.findViewById(R.id.setColorButton);
			setColorButton.setOnClickListener(setColorButtonListener);

			
			currentDialog.show(); // show the dialog
		} // end method showColorDialog
		
				public void showUnitDialog(Dialog cDialog) 
				{
					currentDialog = cDialog;

					// get the unit spinner(dropdown list) and set their onChange listeners
					final Spinner lenspinner = (Spinner) currentDialog
							.findViewById(R.id.lengthSpinner);
					final Spinner velspinner = (Spinner) currentDialog
							.findViewById(R.id.velocitySpinner);
					final Spinner presspinner = (Spinner) currentDialog
							.findViewById(R.id.pressureSpinner);
					

					ArrayAdapter<CharSequence> lenadapter = ArrayAdapter.createFromResource(context,
					        R.array.lenth_unit, android.R.layout.simple_spinner_item);
					// Specify the layout to use when the file list of choices appears
					lenadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// Apply the adapter to the spinner
					lenspinner.setAdapter(lenadapter);
					
					ArrayAdapter<CharSequence> veladapter = ArrayAdapter.createFromResource(context,
					        R.array.velocity_unit, android.R.layout.simple_spinner_item);
					// Specify the layout to use when the file list of choices appears
					lenadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// Apply the adapter to the spinner
					velspinner.setAdapter(veladapter);
					
					ArrayAdapter<CharSequence> presadapter = ArrayAdapter.createFromResource(context,
					        R.array.pressure_unit, android.R.layout.simple_spinner_item);
					// Specify the layout to use when the file list of choices appears
					lenadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// Apply the adapter to the spinner
					presspinner.setAdapter(presadapter);
					
					
					// register event listeners
					lenspinner.setOnItemSelectedListener(lengthSelected);;
					velspinner.setOnItemSelectedListener(velocitySelected);
					presspinner.setOnItemSelectedListener(pressureSelected);
								

					// set the Set Color Button's onClickListener
					Button setColorButton = (Button) currentDialog
							.findViewById(R.id.setUnits);
					setColorButton.setOnClickListener(setUnitButtonListener);

					
					currentDialog.show(); // show the dialog
				} // end method showColorDialog

				
				//implement item selected event for length unit spinner
				private OnItemSelectedListener lengthSelected = new OnItemSelectedListener()
				{

					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int pos, long id) {
						// TODO Auto-generated method stub
						if(pos == 0)
						{
							lengthunit = LengthType.MilliMeter;
						}
						else if(pos == 1)
						{
							lengthunit = LengthType.Inch;
						}
						else if(pos == 2)
						{
							lengthunit = LengthType.Foot;
						}
						else if(pos == 3)
						{
							lengthunit = LengthType.Meter;
						}
						else if(pos == 4)
						{
							lengthunit = LengthType.Kilometer;
						}
						else if(pos == 5)
						{
							lengthunit = LengthType.Mile;
						}
						
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
					
				};
				
				//implement item selected event for velocity unit spinner
				private OnItemSelectedListener velocitySelected = new OnItemSelectedListener()
				{

					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						
						//not yet implemented
						
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
					
				};
				
				//implement item selected event for length unit spinner
				private OnItemSelectedListener pressureSelected = new OnItemSelectedListener()
				{

					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						
						//not yet implemented
						
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
					
				};
		// OnSeekBarChangeListener for the SeekBars in the color dialog
		private OnSeekBarChangeListener colorSeekBarChanged = new OnSeekBarChangeListener() {
			// @Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// get the SeekBars and the colorView LinearLayout
				SeekBar alphaSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.alphaSeekBar);
				SeekBar redSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.redSeekBar);
				SeekBar greenSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.greenSeekBar);
				SeekBar blueSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.blueSeekBar);
				View colorView = (View) currentDialog.findViewById(R.id.colorView);

				// display the current color
				colorView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(),
						redSeekBar.getProgress(), greenSeekBar.getProgress(),
						blueSeekBar.getProgress()));
			} // end method onProgressChanged

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			} // end method onStartTrackingTouch

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			} // end method onStopTrackingTouch
		}; // end colorSeekBarChanged

		// OnClickListener for the color dialog's Set Color Button
		private OnClickListener setColorButtonListener = new OnClickListener() {
			// @Override
			public void onClick(View v) {
				// get the color SeekBars
				SeekBar alphaSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.alphaSeekBar);
				SeekBar redSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.redSeekBar);
				SeekBar greenSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.greenSeekBar);
				SeekBar blueSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.blueSeekBar);

				// set the line color
				setDrawingColor(Color.argb(alphaSeekBar.getProgress(),
						redSeekBar.getProgress(), greenSeekBar.getProgress(),
						blueSeekBar.getProgress()));
				//dialogIsVisible.set(false); // dialog is not on the screen
				currentDialog.dismiss(); // hide the dialog
				currentDialog = null; // dialog no longer needed
			} // end method onClick
		}; // end setColorButtonListener

		private OnClickListener setUnitButtonListener = new OnClickListener() {
			// @Override
			public void onClick(View v) {
				
				currentDialog.dismiss(); // hide the dialog
				currentDialog = null; // dialog no longer needed
			} // end method onClick
		};
		// display a dialog for setting the line width
		public void showLineWidthDialog(Dialog cDialog) 
		{
			// create the dialog and inflate its content
			currentDialog = cDialog;

			// get widthSeekBar and configure it
			SeekBar widthSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.widthSeekBar);
			widthSeekBar.setMax(20);
			widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);
			widthSeekBar.setProgress(getLineWidth());

			// set the Set Line Width Button's onClickListener
			Button setLineWidthButton = (Button) currentDialog
					.findViewById(R.id.widthDialogDoneButton);
			setLineWidthButton.setOnClickListener(setLineWidthButtonListener);

			//dialogIsVisible.set(true); // dialog is on the screen
			currentDialog.show(); // show the dialog
		} // end method showLineWidthDialog

		// OnSeekBarChangeListener for the SeekBar in the width dialog
		private OnSeekBarChangeListener widthSeekBarChanged = new OnSeekBarChangeListener() {
			Bitmap bitmap = Bitmap.createBitmap( // create Bitmap
					400, 100, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap); // associate with Canvas

			// @Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// get the ImageView
				ImageView widthImageView = (ImageView) currentDialog
						.findViewById(R.id.widthImageView);

				// configure a Paint object for the current SeekBar value
				Paint p = new Paint();
				p.setColor(getDrawingColor());
				p.setStrokeCap(Paint.Cap.ROUND);
				p.setStrokeWidth(progress);

				// erase the bitmap and redraw the line
				bitmap.eraseColor(Color.WHITE);
				canvas.drawLine(30, 50, 370, 50, p);
				widthImageView.setImageBitmap(bitmap);
			} // end method onProgressChanged

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			} // end method onStartTrackingTouch

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			} // end method onStopTrackingTouch
		}; // end widthSeekBarChanged

		// OnClickListener for the line width dialog's Set Line Width Button
		private OnClickListener setLineWidthButtonListener = new OnClickListener() {
			// @Override
			public void onClick(View v) {
				// get the color SeekBars
				SeekBar widthSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.widthSeekBar);

				// set the line color
				setLineWidth(widthSeekBar.getProgress());
				
				currentDialog.dismiss(); // hide the dialog
				currentDialog = null; // dialog no longer needed
			} // end method onClick
		}; // end setColorButtonListener
		
		public void showLineCurvatureDialog(Dialog cDialog) 
		{
			// create the dialog and inflate its content
			currentDialog = cDialog;

			// get curvatureSeekBar and configure it
			SeekBar curvatureSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.curvatureSeekBar);
			curvatureSeekBar.setMax(299);
			curvatureSeekBar.setOnSeekBarChangeListener(curvatureSeekBarChanged);
			curvatureSeekBar.setProgress((int)getCurvature());

			// set the Set curvatureButton Button's onClickListener
			Button curvatureButton = (Button) currentDialog
					.findViewById(R.id.curvatureDialogDoneButton);
			curvatureButton.setOnClickListener(curvatureButtonListener);

			currentDialog.show(); // show the dialog
		} 

		// OnSeekBarChangeListener for the SeekBar in the width dialog
		private OnSeekBarChangeListener curvatureSeekBarChanged = new OnSeekBarChangeListener() {
			
			// @Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				

				cdegreeView = (TextView) currentDialog.findViewById(R.id.cdegree);
				cdegreeView.setText(Integer.toString(progress+1));
				
				
			} // end method onProgressChanged

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			} // end method onStartTrackingTouch

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			} // end method onStopTrackingTouch
		}; // end widthSeekBarChanged

		// OnClickListener for the line width dialog's Set Line Width Button
		private OnClickListener curvatureButtonListener = new OnClickListener() {
			// @Override
			public void onClick(View v) {
				// get the color SeekBars
				SeekBar curvatureSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.curvatureSeekBar);

				// set the line color
				setCurvature(curvatureSeekBar.getProgress() +1);
				//dialogIsVisible.set(false); // dialog is not on the screen
				currentDialog.dismiss(); // hide the dialog
				currentDialog = null; // dialog no longer needed
			} // end method onClick
		}; 
		
		
		//function to set whether closet line should snap together
		public void setSnapLines()
		{
			
			String[] list =  {"Yes", "No"};
				
				
			    AlertDialog.Builder builder = new AlertDialog.Builder(context);
			    // Set the dialog title
			    builder.setTitle("Snap lines to the nearest line?")
			    // Specify the filelist array, the items to be selected by default (null for none),
			    // and the listener through which to receive callbacks when items are selected
			           .setSingleChoiceItems(list, 0,
			                      new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
										
						                	   optionSelected = which;
									}
								})
			    // Set the action buttons
			           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {

			            	   if(optionSelected == 0)
			            	   {
			            		   snaptolines = true;
			            	   }
			            	   else
			            	   {
			            		   snaptolines = false;
			            	   }
			                  
			               }
			           })
			           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                  
			               }
			           });
			    
			    builder.show();
		}
		
		//function to set the scale of a drawing
		public void setScale()
	    {
	    	AlertDialog.Builder alert = new AlertDialog.Builder(context);

	    	
	    	input = new EditText(context);
	    	 
	    	alert.setTitle("Set Scale");
	    	alert.setMessage("Please Enter scale");

	    	// Set an EditText view to get user input 
	    	
	    	alert.setView(input);

	    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String value = input.getText().toString();
					scale = Integer.parseInt(value);		
					
				}
			});

	    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    	    // Canceled.
	    	  }
	    	});

	    	alert.show();
	    	 
	    }
		
		// funtion to set line style
		public void setLineStyle()
		{
			
			String[] list =  {"Solid Line", "Dashed Line", "Dotted line ", "Fine_dotted Line"};
				
				
			    AlertDialog.Builder builder = new AlertDialog.Builder(context);
			    // Set the dialog title
			    builder.setTitle("Snap lines to the nearest line?")
			    // Specify the file list array, the items to be selected by default (null for none),
			    // and the listener through which to receive callbacks when items are selected
			           .setSingleChoiceItems(list, 0,
			                      new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
										
						                	   optionSelected = which;
									}
								})
			    // Set the action buttons
			           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {

			            	   if(optionSelected == 0)
			            	   {
			            		   linestyle = lineStyle.solid_line ;
			            	   }
			            	   else if(optionSelected == 1)
			            	   {
			            		   linestyle = lineStyle.dashed_line ;
			            	   }
			            	   else if(optionSelected == 2)
			            	   {
			            		   linestyle = lineStyle.dotted;
			            	   }
			            	   else if(optionSelected == 3)
			            	   {
			            		   linestyle = lineStyle.fine_dotted;
			            	   }
			                  
			               }
			           })
			           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                  
			               }
			           });
			    
			    builder.show();
		}
		
		//display to user settings of selected sprinklers from drop down listbox
		public void showSprinklerSettingDialog(Dialog cDialog, int MinA, int MaxA, int MinR, int MaxR) 
		{
			// create the dialog and inflate its content
			currentDialog = cDialog;
			minA = MinA;
			maxA = MaxA;
			minR = MinR;
			maxR = MaxR;

			angView = (TextView) currentDialog.findViewById(R.id.angleText);
			radView = (TextView)currentDialog.findViewById(R.id.radiusText);
			angView.setText(Integer.toString(minA));
			radView.setText(Integer.toString(minR));
			
			
			SeekBar radiusSeekBar = (SeekBar) currentDialog
					.findViewById(R.id.radius);
			radiusSeekBar.setMax(maxR-minR);
			radiusSeekBar.setOnSeekBarChangeListener(radiusSeekBarChanged);
			
			radiusSeekBar.setProgress(0);
			
			
						SeekBar angleSeekBar = (SeekBar) currentDialog
								.findViewById(R.id.angle);
						angleSeekBar.setMax(maxA- minA);
						angleSeekBar.setOnSeekBarChangeListener(angleSeekBarChanged);
						angleSeekBar.setProgress(0);
						

			// set the setSprinklerButton  Button's onClickListener
			Button setSprinklerButton = (Button) currentDialog
					.findViewById(R.id.setSprinkler);
			setSprinklerButton.setOnClickListener(setSprinklerButtonListener);

			currentDialog.show(); // show the dialog
			
			
		}
		
		private OnSeekBarChangeListener radiusSeekBarChanged = new OnSeekBarChangeListener() {
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				
				int radius = minR + progress;
				radView.setText(Integer.toString(radius));
				
				Radius = convertLength(radius, LengthType.Foot, lengthunit );
				
				
				
			} // end method onProgressChanged

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			} // end method onStartTrackingTouch

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			} // end method onStopTrackingTouch
		};
		
private OnSeekBarChangeListener angleSeekBarChanged = new OnSeekBarChangeListener() {
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				
				int angle = minA + progress;
				angView.setText(Integer.toString(angle));
				Angle = angle;
				
			} // end method onProgressChanged

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			} // end method onStartTrackingTouch

			// required method of interface OnSeekBarChangeListener
			// @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			} // end method onStopTrackingTouch
		};
		
		//listener for setSprinklerButtonListener
		private OnClickListener setSprinklerButtonListener = new OnClickListener() {
			// @Override
			public void onClick(View v) {
				
				SeekBar radiusSeekBar = (SeekBar) currentDialog
						.findViewById(R.id.radius);
				
				
				
				// get widthSeekBar and configure it
							SeekBar angleSeekBar = (SeekBar) currentDialog
									.findViewById(R.id.angle);
							
				
				int angle = minA + angleSeekBar.getProgress();
				angView.setText(Integer.toString(angle));
				Angle = angle;
				
				int radius = minR + radiusSeekBar.getProgress();
				
				
				Radius = convertLength(radius, LengthType.Foot, lengthunit );
				setSprinklerRadius(convertLength(radius, LengthType.Foot, lengthunit ));
				
				currentDialog.dismiss(); // hide the dialog
				return;
				//currentDialog = null; // dialog no longer needed
			} // end method onClick
		};
		
		//returns conversion factor for a particular unit
		 public double GetLenConversionFactor(LengthType measurementType)
	        {
	            switch (measurementType)
	            {
	                case MilliMeter:
	                    return 1000000;
	                case Inch:
	                    return 39370.07874;
	                case Foot:
	                    return 3280.839895;
	                case Meter:
	                    return 1000;
	                case Kilometer:
	                    return 1;
	                case Mile:
	                    return 0.6213711922;
	            }
				return 1000;

	           
	        }
		 
		 //convert length from one unit to another
		 public double convertLength(double value, LengthType fromUnit, LengthType toUnit )
		 {
			 
			 double baseFrom = GetLenConversionFactor(fromUnit);
			 double baseTo =  GetLenConversionFactor(toUnit);
			 
			 return baseTo *value / baseFrom;
		 }
}
