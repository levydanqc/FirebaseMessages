package com.example.geomessages.ui.maps;

import static java.lang.Double.parseDouble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.geomessages.R;
import com.example.geomessages.databinding.FragmentMapsBinding;
import com.example.geomessages.model.Message;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;

public class MapsFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapLongClickListener {

    private static final int LOCATION_PERMISSION_CODE = 1234;
    private FragmentMapsBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location userLocation;
    private TextView tvDistance;
    private MapsViewModel mapsViewModel;
    private LatLng selected;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapsViewModel = new ViewModelProvider(requireActivity()).get(MapsViewModel.class);

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    userLocation = location;
                }
            }
        };

        mapsViewModel.getMessages().observe(getViewLifecycleOwner(), messages ->
        {
            if (mMap != null) {
                mMap.clear();
                for (Message marker : messages) {
                    LatLng latLng = new LatLng(parseDouble(marker.getLatitude()), parseDouble(marker.getLongitude()));
                    Objects.requireNonNull(mMap.addMarker(new MarkerOptions().position(latLng).title(marker.getMessage())))
                            .setTag(marker.getPicture());
                }
            }
        });

        tvDistance = root.findViewById(R.id.tv_distance);
        tvDistance.setVisibility(View.INVISIBLE);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().size() > 0) {
            String lat = MapsFragmentArgs.fromBundle(getArguments()).getLatitude();
            String lon = MapsFragmentArgs.fromBundle(getArguments()).getLongitude();
            selected = new LatLng(parseDouble(lat), parseDouble(lon));
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(this);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLongClickListener(this);
        enableLocation();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }

        if (selected != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selected, 15));
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        }
                    });
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        mapsViewModel.getMessages().observe(getViewLifecycleOwner(),
                messagesList -> {
                    for (Message marker : messagesList) {
                        LatLng latLng = new LatLng(parseDouble(marker.getLatitude()), parseDouble(marker.getLongitude()));
                        Objects.requireNonNull(mMap.addMarker(new MarkerOptions().position(latLng).title(marker.getMessage())))
                                .setTag(marker.getPicture());
                    }
                });

    }

    @SuppressLint("MissingPermission")
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog.setTitle("Accès à la localisation");
                    dialog.setMessage("L'accès à la localisation est nécéssaire pour la géolocalisation");
                    dialog.setPositiveButton("Ok", (dialog1, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE));
                    dialog.setNegativeButton("Annuler", (dialog12, which) -> Toast.makeText(requireActivity(), "Impossible de géolocaliser.", Toast.LENGTH_SHORT).show());
                    dialog.show();
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Location location = new Location("Marker");
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);

        float distance = userLocation.distanceTo(location);
        tvDistance.setVisibility(View.VISIBLE);
        tvDistance.setText(String.format(Locale.CANADA_FRENCH, "Distance: %.2fkm", distance / 1000));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            TranslateAnimation animate = new TranslateAnimation(0, 0, 0, 2 * tvDistance.getHeight());
            animate.setDuration(500);
            tvDistance.startAnimation(animate);
            tvDistance.setVisibility(View.INVISIBLE);
        }, 3000);

    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.marker_layout, null);
        TextView tvMarker = view.findViewById(R.id.tv_marker);
        tvMarker.setText(marker.getTitle());
        ImageView ivMarker = view.findViewById(R.id.iv_marker);
        String url = Objects.requireNonNull(marker.getTag()).toString();
        Picasso.get().load(url)
                .fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get().load(url)
                                .placeholder(R.drawable.ic_baseline_account_circle_24)
                                .into(ivMarker);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_baseline_account_circle_24)
                                .into(ivMarker);
                    }
                });

        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(""));

        NavigationView navigationView = (NavigationView) requireActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        String nom = ((TextView) headerView.findViewById(R.id.tv_nom)).getText().toString();
        String prenom = ((TextView) headerView.findViewById(R.id.tv_prenom)).getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Ajout d'un marker");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Titre");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            assert marker != null;
            if (input.getText().toString().equals("")) {
                marker.remove();
                return;
            }
            marker.setTitle(input.getText().toString());
            String url = mapsViewModel.addMarker(marker, prenom, nom);
            marker.setTag(url);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            assert marker != null;
            marker.remove();
            dialog.cancel();
        });

        builder.show();

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

}