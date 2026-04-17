var Config = {
  sms: {
    http: {
      postUrl: "/dcpService_v3x/DCP/services/invoke",
      locationUrl: "/retail/",
      emailUrl: "",
      goodImagesUrl: "/dcpService_v3x/goodsimages/",
      reportImagesUrl: "/dcpService_v3x/baosunimages/",
      dualUrl: "/dcpService_v3x/dualplay/",
      GDkey: "444eac94a39491d4d336ff8050a2e835",
      documentUploadUrl: "/resource/shopimage/",
    },
  },
  crm: {
    http: {
      postUrl: "/crmService_v3x/sposWeb/crm/invoke",
      locationUrl: "/",
      uploadUrl: "/crmService_v3x/sposWeb/crm/UploadMedia",
    },
  },
  supplier: {
    http: {
      postUrl: "",
    },
  },
  sale: {
    http: {
      postUrl: "/promService_v3x/prom_rules",
      basicmarketUrl: "/promService_v3x/prom_rules",
      basicmarketSign: "abcd",
    },
  },
  mes: {
    apiUserCode: "digiwin",
    http: {
      postUrl: "/mesService_v3x/MES/services/invoke",
    },
  },
  eip: {
    apiUserCode: "digiwin",
    http: {
      postUrl: "/eipService/EIP/services/invoke",
    },
  },
  isWechat: false,
  httpsAuto: false,
};

// 导出配置（ES Module 规范）
export default Config;
