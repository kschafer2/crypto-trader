package com.protonmail.kschay.cryptotrader.domain.order;


import com.protonmail.kschay.cryptotrader.domain.execute.ExecuteOrder;

public interface OrderClient {
    void placeOrder(ExecuteOrder executeOrder);
}
