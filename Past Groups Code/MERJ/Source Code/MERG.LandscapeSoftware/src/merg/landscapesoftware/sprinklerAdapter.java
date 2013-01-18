package merg.landscapesoftware;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//custom adapter class for list object placed on listview or gridview
public class sprinklerAdapter extends ArrayAdapter<Sprinkler>
{

	Context context; 
    int layoutResourceId; 
	private ArrayList<Sprinkler> items;
	 private LayoutInflater inflater;

	public sprinklerAdapter(Context context, int textViewResourceId,
			List<Sprinkler> items)
	{
		super(context, textViewResourceId, items);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.items = (ArrayList<Sprinkler>) items;
		
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
	{
        View v = convertView;
       
        
        if(v == null)
        {
        	v = new View(context);
        	v = inflater.inflate(R.layout.item_layout, parent, false);
        
        	 TextView textView1 = (TextView)v.findViewById(R.id.item1);
        	 textView1.setText(("oleku"));
        	 
        	 TextView textView2 = (TextView)v.findViewById(R.id.item2);
        	 String manu = (items.get(position).manufactuter);
        	 textView2.setText(manu);
        	 
        	 TextView textView3 = (TextView)v.findViewById(R.id.item3);
        	 String model = items.get(position).model;
        	 textView3.setText(model);
        	 
        	 TextView textView4 = (TextView)v.findViewById(R.id.item4);
        	textView4.setText((items.get(position).sprinklerType));
        	 
        	 TextView textView5 = (TextView)v.findViewById(R.id.item5);
        	textView5.setText(String.valueOf(items.get(position).minRaius));
        	 
        	 TextView textView6 = (TextView)v.findViewById(R.id.item6);
        	textView6.setText(String.valueOf(items.get(position).maxRadius));
        	 
        	 TextView textView7 = (TextView)v.findViewById(R.id.item7);
        	 textView7.setText(String.valueOf(items.get(position).minAngle));
        	 
        	 TextView textView8 = (TextView)v.findViewById(R.id.item8);
        	 textView8.setText(String.valueOf(items.get(position).minAngle));
        	 
        	 TextView textView9 = (TextView)v.findViewById(R.id.item9);
        	 textView9.setText(String.valueOf(items.get(position).price));

        	
        	
            
        }
       
       
		

            return v;
            
	}
}
