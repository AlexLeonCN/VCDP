package com.neonnexus.vcdp.common;

/**
 * 错误常量接口。
 * 错误码为 8 位：前 4 位为业务场景（从 1000 递增），后 4 位为错误序号（从 0001 递增）。
 */
public interface ErrorConstant {

    interface Common {
        Pair<Integer, String> BAD_REQUEST = Pair.of(10000001, "请求参数错误");
        Pair<Integer, String> NOT_FOUND = Pair.of(10000002, "资源不存在");
        Pair<Integer, String> INTERNAL_ERROR = Pair.of(10000003, "系统内部错误，请稍后重试");
        Pair<Integer, String> UNPROCESSABLE_ENTITY = Pair.of(10000004, "请求实体不可处理");
    }

    interface Project {
        Pair<Integer, String> NAME_EMPTY = Pair.of(10010001, "工程名称不能为空");
        Pair<Integer, String> NAME_DUPLICATE = Pair.of(10010002, "工程名称已存在");
        Pair<Integer, String> NOT_FOUND = Pair.of(10010003, "工程不存在");
    }

    interface Ecu {
        Pair<Integer, String> PROJECT_NOT_FOUND = Pair.of(10020001, "关联工程不存在");
        Pair<Integer, String> CONFIG_EMPTY = Pair.of(10020002, "ECU基础配置不能为空");
        Pair<Integer, String> NOT_FOUND = Pair.of(10020003, "ECU不存在");
        Pair<Integer, String> NAME_EMPTY = Pair.of(10020004, "ECU名称不能为空");
        Pair<Integer, String> NAME_DUPLICATE = Pair.of(10020005, "同工程下ECU名称不可重复");
        Pair<Integer, String> MAC_EMPTY = Pair.of(10020006, "MAC地址不能为空");
        Pair<Integer, String> MAC_INVALID = Pair.of(10020007, "MAC地址格式不正确");
        Pair<Integer, String> MAC_DUPLICATE = Pair.of(10020008, "同工程下ECU的MAC地址不可重复");
        Pair<Integer, String> IP_EMPTY = Pair.of(10020009, "IP地址不能为空");
        Pair<Integer, String> IP_INVALID = Pair.of(10020010, "IP地址格式不正确");
        Pair<Integer, String> IP_DUPLICATE = Pair.of(10020011, "同工程下ECU的IP地址不可重复");
        Pair<Integer, String> PORT_EMPTY = Pair.of(10020012, "端口号不能为空");
        Pair<Integer, String> PORT_INVALID = Pair.of(10020013, "端口号必须是大于等于0的整数");
        Pair<Integer, String> PORT_DUPLICATE = Pair.of(10020014, "同工程下ECU的端口号不可重复");
        Pair<Integer, String> INDEX_EMPTY = Pair.of(10020015, "设备索引不能为空");
        Pair<Integer, String> INDEX_INVALID = Pair.of(10020016, "设备索引必须是大于等于0的整数");
        Pair<Integer, String> INDEX_DUPLICATE = Pair.of(10020017, "同工程下ECU的设备索引不可重复");
    }

    interface EcuForward {
        Pair<Integer, String> CONFIG_EMPTY = Pair.of(10030001, "转发表通信配置不能为空");
        Pair<Integer, String> P_FLASH_START_INVALID = Pair.of(10030002, "pFlash空间起始地址必须是大于0的十六进制数");
        Pair<Integer, String> P_FLASH_SIZE_INVALID = Pair.of(10030003, "pFlash空间大小必须是大于0的十六进制数");
        Pair<Integer, String> RAM_START_INVALID = Pair.of(10030004, "RAM空间起始地址必须是大于0的十六进制数");
        Pair<Integer, String> RAM_SIZE_INVALID = Pair.of(10030005, "RAM空间大小必须是大于0的十六进制数");
    }

    interface CanInterface {
        Pair<Integer, String> NAME_EMPTY = Pair.of(10040001, "CAN接口名称不能为空");
        Pair<Integer, String> NAME_DUPLICATE = Pair.of(10040002, "同工程同ECU下接口名称不可重复");
        Pair<Integer, String> CHANNEL_EMPTY = Pair.of(10040003, "CAN接口通道ID不能为空");
        Pair<Integer, String> CHANNEL_INVALID = Pair.of(10040004, "CAN接口通道ID必须是大于等于0的整数");
        Pair<Integer, String> CHANNEL_DUPLICATE = Pair.of(10040005, "同工程同ECU下CAN接口通道ID不可重复");
        Pair<Integer, String> PORT_EMPTY = Pair.of(10040006, "CAN接口端口号不能为空");
        Pair<Integer, String> PORT_INVALID = Pair.of(10040007, "CAN接口端口号必须是大于等于0的整数");
        Pair<Integer, String> PORT_DUPLICATE = Pair.of(10040008, "同工程同ECU下接口端口号不可重复");
        Pair<Integer, String> TYPE_INVALID = Pair.of(10040009, "CAN接口类型不合法");
        Pair<Integer, String> CONN_TYPE_INVALID = Pair.of(10040010, "CAN接口连接类型不合法");
    }

    interface LinInterface {
        Pair<Integer, String> NAME_EMPTY = Pair.of(10050001, "LIN接口名称不能为空");
        Pair<Integer, String> NAME_DUPLICATE = Pair.of(10050002, "同工程同ECU下接口名称不可重复");
        Pair<Integer, String> CHANNEL_EMPTY = Pair.of(10050003, "LIN接口通道ID不能为空");
        Pair<Integer, String> CHANNEL_INVALID = Pair.of(10050004, "LIN接口通道ID必须是大于等于0的整数");
        Pair<Integer, String> CHANNEL_DUPLICATE = Pair.of(10050005, "同工程同ECU下LIN接口通道ID不可重复");
        Pair<Integer, String> PORT_EMPTY = Pair.of(10050006, "LIN接口端口号不能为空");
        Pair<Integer, String> PORT_INVALID = Pair.of(10050007, "LIN接口端口号必须是大于等于0的整数");
        Pair<Integer, String> PORT_DUPLICATE = Pair.of(10050008, "同工程同ECU下接口端口号不可重复");
    }

    interface EthInterface {
        Pair<Integer, String> NAME_EMPTY = Pair.of(10060001, "ETH接口名称不能为空");
        Pair<Integer, String> NAME_DUPLICATE = Pair.of(10060002, "同工程同ECU下接口名称不可重复");
        Pair<Integer, String> CHANNEL_EMPTY = Pair.of(10060003, "ETH接口通道ID不能为空");
        Pair<Integer, String> CHANNEL_INVALID = Pair.of(10060004, "ETH接口通道ID必须是大于等于0的整数");
        Pair<Integer, String> CHANNEL_DUPLICATE = Pair.of(10060005, "同工程同ECU下ETH接口通道ID不可重复");
        Pair<Integer, String> PORT_EMPTY = Pair.of(10060006, "ETH接口端口号不能为空");
        Pair<Integer, String> PORT_INVALID = Pair.of(10060007, "ETH接口端口号必须是大于等于0的整数");
        Pair<Integer, String> PORT_DUPLICATE = Pair.of(10060008, "同工程同ECU下接口端口号不可重复");
        Pair<Integer, String> TYPE_INVALID = Pair.of(10060009, "ETH接口类型不合法");
    }
}
