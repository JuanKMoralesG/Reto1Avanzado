package com.example.reto1pizzeria.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.reto1pizzeria.ProductActivity;
import com.example.reto1pizzeria.R;
import com.example.reto1pizzeria.model.Product;

import java.util.List;


public class ProductAdapter extends ArrayAdapter<Product> {

    // Recursos de diseño y contexto
    private int resourceLayout;
    private Context mContext;

    // Bandera para controlar la visibilidad de los botones
    private boolean hideButtons = false;

    // Constructor del adaptador
    public ProductAdapter(Context context, int resource, List<Product> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    // Método para establecer si los botones deben ocultarse o mostrarse
    public void setHideButtons(boolean hide) {
        hideButtons = hide;
    }

    // Método para obtener la vista del adaptador para un elemento específico

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        // Si la vista es nula, inflar el diseño
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(resourceLayout, parent, false);
        }

        // Obtener el producto actual
        Product product = getItem(position);

        // Si el producto es válido, establecer los valores en la vista
        if (product != null) {
            // Obtener las vistas de nombre y precio
            TextView textViewName = view.findViewById(R.id.textViewName);
            TextView textViewPrice = view.findViewById(R.id.textViewPrice);
            TextView textViewDescription = view.findViewById(R.id.textViewDescrip);
            TextView textViewCant = view.findViewById(R.id.textViewCant);

            // Obtener los botones de edición y eliminación
            Button buttonEdit = view.findViewById(R.id.buttonEdit);
            Button buttonDelete = view.findViewById(R.id.buttonDelete);

            // Establecer el nombre y el precio del producto en los TextView
            textViewName.setText(product.getName());
            textViewPrice.setText(String.format(mContext.getString(R.string.price_format), product.getPrice()));
            textViewDescription.setText(product.getDescription());
            textViewCant.setText(String.format(mContext.getString(R.string.price_format), product.getCant()));

            // Controlar la visibilidad de los botones según la variable hideButtons
            if (hideButtons) {
                buttonEdit.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
            } else {
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }

            // Asignar listeners a los botones
            buttonEdit.setOnClickListener(v -> {
                // Llamar al método editProduct de MainActivity con el producto a editar
                if (mContext instanceof ProductActivity) {
                    ((ProductActivity) mContext).editProduct(product);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                // Llamar al método deleteProduct de MainActivity con el producto a eliminar
                if (mContext instanceof ProductActivity) {
                    ((ProductActivity) mContext).deleteProduct(product);
                }
            });
        }
        return view;
    }
}