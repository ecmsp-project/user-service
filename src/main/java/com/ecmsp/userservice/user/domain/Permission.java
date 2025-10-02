package com.ecmsp.userservice.user.domain;

public enum Permission {
    // Product permissions
    WRITE_PRODUCTS,
    DELETE_PRODUCTS,

    // Order permissions
    READ_ORDERS,
    WRITE_ORDERS,
    CANCEL_ORDERS,

    // User management permissions
    MANAGE_USERS,

    // Role management permissions
    MANAGE_ROLES,

    // Payment permissions
    PROCESS_PAYMENTS,
    REFUND_PAYMENTS
}
