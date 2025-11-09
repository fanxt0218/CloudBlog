package com.cloudblog.common.pojo.Dto;

import com.cloudblog.common.enums.SocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessage<T> {

    private SocketMessageType type;

    private T data;
}
