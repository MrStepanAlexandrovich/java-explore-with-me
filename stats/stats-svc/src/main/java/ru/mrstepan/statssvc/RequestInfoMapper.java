package ru.mrstepan.statssvc;

import ru.mrstepan.statsdto.RequestInfoDto;

public class RequestInfoMapper {
    public static RequestInfo toRequestInfo(RequestInfoDto requestInfoDto) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setIp(requestInfoDto.getIp());
        requestInfo.setApp(requestInfoDto.getApp());
        requestInfo.setUri(requestInfoDto.getUri());
        requestInfo.setTimestamp(requestInfoDto.getTimestamp());

        return requestInfo;
    }
}
