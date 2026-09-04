package com.uasdisprog.backend.service;

import com.uasdisprog.backend.entity.Customer;
import com.uasdisprog.backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TcpNotificationClient tcpNotificationClient;

    @Transactional(readOnly = true)
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public Double getBalance(Integer customerId) {
        Customer customer = getCustomerById(customerId);
        return customer.getBalance();
    }

    @Transactional
    public Customer topupBalance(Integer customerId, Double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        Customer customer = getCustomerById(customerId);
        Double oldBalance = customer.getBalance();
        customer.setBalance(customer.getBalance() + amount);
        Customer saved = customerRepository.save(customer);

        // Send notification via TCP
        String message = "Topup saldo berhasil! Saldo Anda sekarang: Rp " + customer.getBalance().longValue();
        tcpNotificationClient.sendPersonalNotification(customerId, message);

        return saved;
    }

    @Transactional
    public Customer deductBalance(Integer customerId, Double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        Customer customer = getCustomerById(customerId);
        
        if (customer.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        customer.setBalance(customer.getBalance() - amount);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer refundBalance(Integer customerId, Double amount) {
        return topupBalance(customerId, amount);
    }
}
