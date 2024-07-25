package com.example.reto1pizzeria.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.reto1pizzeria.ProductActivity;
import com.example.reto1pizzeria.R;
import com.example.reto1pizzeria.model.Product;

import java.util.List;

public class PedidoAdapter extends ArrayAdapter<Product> {
    // Recursos de diseño y contexto
    private int resourceLayout;
    private Context mContext;

    // Bandera para controlar la visibilidad de los botones
    private boolean hideButtons = false;

    // Constructor del adaptador
    public PedidoAdapter(Context context, int resource, List<Product> items) {
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
            TextView textViewName = view.findViewById(R.id.textViewNameItem);
            TextView textViewPrice = view.findViewById(R.id.textViewPriceItem);
            TextView textViewDescription = view.findViewById(R.id.textViewDescripItem);

            // Obtener los botones de edición y eliminación
            ImageButton buttonAdd = view.findViewById(R.id.buttonAddItem);
            ImageButton buttonDelete = view.findViewById(R.id.buttonDeleteItem);

            // Establecer el nombre y el precio del producto en los TextView
            textViewName.setText(product.getName());
            textViewPrice.setText(String.format(mContext.getString(R.string.price_format), product.getPrice()));
            textViewDescription.setText(product.getDescription());

            // Controlar la visibilidad de los botones según la variable hideButtons
            if (hideButtons) {
                buttonAdd.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
            } else {
                buttonAdd.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }

            // Asignar listeners a los botones
            buttonAdd.setOnClickListener(v -> {
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