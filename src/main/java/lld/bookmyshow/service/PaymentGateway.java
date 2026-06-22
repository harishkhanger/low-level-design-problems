package lld.bookmyshow.service;

import lld.bookmyshow.model.*;

public interface PaymentGateway {
    boolean pay(User user);
}
