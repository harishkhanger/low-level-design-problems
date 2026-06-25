package lld.notification.decorator;

import lld.notification.channel.Channel;

public abstract class ChannelDecorator implements Channel {
    protected final Channel wrapped;

    protected ChannelDecorator(Channel wrapped) {
        this.wrapped = wrapped;
    }
}
