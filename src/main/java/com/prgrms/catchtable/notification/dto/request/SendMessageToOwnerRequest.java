package com.prgrms.catchtable.notification.dto.request;

import com.prgrms.catchtable.owner.domain.Owner;
import lombok.Builder;

@Builder
public record SendMessageToOwnerRequest(Owner owner,
                                        String content) {

}
