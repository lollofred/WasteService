%====================================================================================
% wastservice description   
%====================================================================================
context(ctxwasteservice, "localhost",  "TCP", "8032").
 qactor( wasteservice, ctxwasteservice, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxwasteservice, "it.unibo.transporttrolley.Transporttrolley").
