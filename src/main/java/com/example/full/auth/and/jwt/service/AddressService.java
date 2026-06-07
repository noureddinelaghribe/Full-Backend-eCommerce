package com.example.full.auth.and.jwt.service;


import com.example.full.auth.and.jwt.dto.*;
import com.example.full.auth.and.jwt.model.*;
import com.example.full.auth.and.jwt.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;  // injected

    public Page<AddressResponse> getAllAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable)//.stream()
                .map(this::convertToAddressResponse);
                //.collect(Collectors.toList());
    }


    public AddressResponse getMyDefaultAddress() {
        User currentUser = getCurrentUser();
        Address address = addressRepository.findByUserAndIsDefaultTrue(currentUser)
                .orElseThrow(() -> new RuntimeException("No default address found"));
        return convertToAddressResponse(address);
    }



    public Long getIdMyDefaultAddress() {
        User currentUser = getCurrentUser();
        Address address = addressRepository.findByUserAndIsDefaultTrue(currentUser)
                .orElseThrow(() -> new RuntimeException("No default address found"));
        Long idAddress = address.getId();
        return idAddress;
    }


    public Page<AddressResponse> getMyAddresses(Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        User currentUser = getCurrentUser();

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return addressRepository.findByUser(currentUser, pageable)//.stream()
                .map(this::convertToAddressResponse);
                //.collect(Collectors.toList());
    }


    public Page<AddressResponse> getUserAddresses(Long id, Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext

        User user = userService.getUserEntityById(id);

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return addressRepository.findByUser(user, pageable)//.stream()
                .map(this::convertToAddressResponse);
                //.collect(Collectors.toList());
    }


    public AddressResponse getAddressesById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        return convertToAddressResponse(address);
    }


    public AddressResponse createAddresses(AddressRequest request) {

        User currentUser = getCurrentUser();

        // إذا كان العنوان الجديد Default
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.clearDefaultAddresses(currentUser.getId());
        }

        Address address = Address.builder()
                .user(currentUser)
                .state(request.getState())
                .street(request.getStreet())
                .country(request.getCountry())
                .city(request.getCity())
                .isDefault(request.getIsDefault())
                .build();
        Address savedAddress = addressRepository.save(address);
        return convertToAddressResponse(savedAddress);

    }


    public AddressResponse updateAddress(Long id, AddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);

        // Only admin can update all products, sellers can only update their own products
        if (!isAdmin && !address.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only update your own Address.");
        }

        // إذا كان العنوان الجديد Default
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.clearDefaultAddresses(currentUser.getId());
        }

        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setStreet(request.getStreet());
        address.setState(request.getState());
        address.setIsDefault(request.getIsDefault());

        Address updatedaddress = addressRepository.save(address);
        return convertToAddressResponse(updatedaddress);
    }


    public AddressResponse updateAddress(Long id, AddressDefaultRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);

        // Only admin can update all products, sellers can only update their own products
        if (!isAdmin && !address.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only update your own Address.");
        }

        // إذا كان العنوان الجديد Default
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.clearDefaultAddresses(currentUser.getId());
        }

        address.setIsDefault(request.getIsDefault());

        Address updatedaddress = addressRepository.save(address);
        return convertToAddressResponse(updatedaddress);
    }

    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found with id: " + id);
        }

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);

        // Only admin can delete all products, sellers can only delete their own products
        if (!isAdmin && !address.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only delete your own Address.");
        }

        addressRepository.deleteById(id);
    }





    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }

        return (User) authentication.getPrincipal();
    }

    /**
     * Check if current user has admin role
     */
    private boolean isAdminUser(User user) {
        return user != null && Role.ROLE_ADMIN.equals(user.getRole());
    }


    private boolean isSellerUser(User user) {
        return user != null && Role.ROLE_SELLER.equals(user.getRole());
    }

    private AddressResponse convertToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }


}
