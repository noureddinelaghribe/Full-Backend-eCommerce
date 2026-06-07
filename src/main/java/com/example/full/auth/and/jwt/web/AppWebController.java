package com.example.full.auth.and.jwt.web;

import com.example.full.auth.and.jwt.dto.AddressDefaultRequest;
import com.example.full.auth.and.jwt.dto.AddressRequest;
import com.example.full.auth.and.jwt.dto.CartCreateRequest;
import com.example.full.auth.and.jwt.dto.UpdateUserRequest;
import com.example.full.auth.and.jwt.dto.AddressResponse;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.service.AddressService;
import com.example.full.auth.and.jwt.service.CartService;
import com.example.full.auth.and.jwt.service.OrderService;
import com.example.full.auth.and.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppWebController {

    private static User requireUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    private final CartService cartService;
    private final UserService userService;
    private final AddressService addressService;
    private final OrderService orderService;

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cartPage", cartService.getMyCartProducts(
                PageRequest.of(0, 100, Sort.by("id").descending())));
        return "app/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, RedirectAttributes ra) {
        try {
            cartService.addToCart(CartCreateRequest.builder().productId(productId).build());
            ra.addFlashAttribute("message", "Added to cart.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/cart";
    }

    @PostMapping("/cart/increase/{cartId}")
    public String increase(@PathVariable Long cartId, RedirectAttributes ra) {
        try {
            cartService.updateProductCartIncrease(cartId);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/cart";
    }

    @PostMapping("/cart/decrease/{cartId}")
    public String decrease(@PathVariable Long cartId, RedirectAttributes ra) {
        try {
            cartService.updateProductCartDecrease(cartId);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/cart";
    }

    @PostMapping("/cart/remove/{cartId}")
    public String removeLine(@PathVariable Long cartId, RedirectAttributes ra) {
        try {
            cartService.deleteCart(cartId);
            ra.addFlashAttribute("message", "Item removed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(RedirectAttributes ra) {
        try {
            cartService.clearCart();
            ra.addFlashAttribute("message", "Cart cleared.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/cart";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = requireUser();
        model.addAttribute("user", userService.getUserById(currentUser.getId()));
        if (!model.containsAttribute("updateUser")) {
            model.addAttribute("updateUser", new UpdateUserRequest());
        }
        return "app/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(UpdateUserRequest updateUser, RedirectAttributes ra) {
        try {
            userService.updateUser(requireUser().getId(), updateUser);
            ra.addFlashAttribute("message", "Profile updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/profile";
    }

    @GetMapping("/addresses")
    public String addresses(Model model) {
        model.addAttribute("addresses", addressService.getMyAddresses(
                PageRequest.of(0, 50, Sort.by("id").descending())));
        return "app/addresses";
    }

    @GetMapping("/addresses/new")
    public String newAddress(Model model) {
        model.addAttribute("addressForm", AddressRequest.builder()
                .street("")
                .city("")
                .state("")
                .country("")
                .isDefault(false)
                .build());
        model.addAttribute("isEdit", false);
        return "app/address-form";
    }

    @GetMapping("/addresses/{id}/edit")
    public String editAddress(@PathVariable Long id, Model model) {
        AddressResponse ar = addressService.getAddressesById(id);
        model.addAttribute("addressForm", AddressRequest.builder()
                .street(ar.getStreet())
                .city(ar.getCity())
                .state(ar.getState())
                .country(ar.getCountry())
                .isDefault(ar.getIsDefault())
                .build());
        model.addAttribute("isEdit", true);
        model.addAttribute("editId", id);
        return "app/address-form";
    }

    @PostMapping("/addresses")
    public String createAddress(@ModelAttribute AddressRequest form, RedirectAttributes ra) {
        try {
            addressService.createAddresses(form);
            ra.addFlashAttribute("message", "Address saved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/addresses";
    }

    @PostMapping("/addresses/{id}")
    public String updateAddress(@PathVariable Long id, @ModelAttribute AddressRequest form, RedirectAttributes ra) {
        try {
            addressService.updateAddress(id, form);
            ra.addFlashAttribute("message", "Address updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/addresses";
    }

    @PostMapping("/addresses/{id}/delete")
    public String deleteAddress(@PathVariable Long id, RedirectAttributes ra) {
        try {
            addressService.deleteAddress(id);
            ra.addFlashAttribute("message", "Address deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/addresses";
    }

    @PostMapping("/addresses/{id}/default")
    public String setDefault(@PathVariable Long id, RedirectAttributes ra) {
        try {
            addressService.updateAddress(id, AddressDefaultRequest.builder().isDefault(true).build());
            ra.addFlashAttribute("message", "Default address updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/addresses";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getMyOrders(
                PageRequest.of(0, 50, Sort.by("id").descending())));
        return "app/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getMyOrderById(id));
        return "app/order-detail";
    }

    @PostMapping("/orders/new")
    public String placeOrder(RedirectAttributes ra) {
        try {
            var order = orderService.createOrder();
            ra.addFlashAttribute("message", "Order placed successfully.");
            return "redirect:/app/orders/" + order.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/cart";
        }
    }

//    @PostMapping("/orders/{id}/cancel")
//    public String cancelOrder(@PathVariable Long id, RedirectAttributes ra) {
//        try {
//            orderService.cancelMyOrder(id);
//            ra.addFlashAttribute("message", "Order cancelled.");
//        } catch (Exception e) {
//            ra.addFlashAttribute("error", e.getMessage());
//        }
//        return "redirect:/app/orders/" + id;
//    }
}
