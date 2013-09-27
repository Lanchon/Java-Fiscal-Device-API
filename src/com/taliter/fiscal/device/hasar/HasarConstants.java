package com.taliter.fiscal.device.hasar;

/** A set of constants used to communicate with Hasar fiscal devices such as fiscal printers. */
public interface HasarConstants
{
	//// CODIGOS DE COMANDO //////////////////

	// Comandos de configuración y consulta

	/** Consultar estado. (0x2A) */
	public int CMD_STATUS_REQUEST             = 0x2A;
	/** Consultar estado intermedio. (0xA1) */
	public int CMD_STATPRN                    = 0xA1;
	/** Consultar versión del controlador fiscal. (0x7F) */
	public int CMD_GET_FISCAL_DEVICE_VERSION  = 0x7F;

	///** Dar formato a la memoria fiscal. (0x--) */
	//public int CMD_FORMAT_FISCAL_MEMORY       = 0x--;
	///** Dar de alta la memoria fiscal. (0x--) */
	//public int CMD_INIT_FISCAL_MEMORY         = 0x--;
	/** Dar de baja la memoria fiscal. (0xB1) */
	public int CMD_KILL_FISCAL_MEMORY         = 0xB1;

	/** Cambiar velocidad de comunicación. (0xA0) */
	public int CMD_SET_BAUD_RATE              = 0xA0;

	/** Cambiar fecha y hora. (0x58) */
	public int CMD_SET_DATE_TIME              = 0x58;
	/** Consultar fecha y hora. (0x59) */
	public int CMD_GET_DATE_TIME              = 0x59;

	/** Cargar configuración, comando nuevo. (0x95) */
	public int CMD_SET_GENERAL_CONFIGURATION  = 0x95;
	/** Consultar configuración, comando nuevo. (0x96) */
	public int CMD_GET_GENERAL_CONFIGURATION  = 0x96;
	/** Cargar configuración, comando viejo. (0x65) */
	public int CMD_SET_CONFIGURATION_BY_BLOCK = 0x65;
	/** Consultar configuración, comando viejo. (0x66) */
	public int CMD_GET_CONFIGURATION_BY_BLOCK = 0x66;
	/** Cargar configuración por parámetro. (0x64) */
	public int CMD_SET_CONFIGURATION_BY_ONE   = 0x64;

	/** Consultar datos de inicialización. (0x73) */
	public int CMD_GET_INIT_DATA              = 0x73;
	/** Cambiar responsabilidad frente al IVA. (0x63) */
	public int CMD_CHANGE_IVA_RESPONSABILITY  = 0x63;
	/** Cambiar número de Ingresos Brutos. (0x6E) */
	public int CMD_CHANGE_IB_NUMBER           = 0x6E;

	/** Cargar nombre de fantasía del propietario. (0x5F) */
	public int CMD_SET_FANTASY_NAME           = 0x5F;
	/** Consultar nombre de fantasía del propietario. (0x92) */
	public int CMD_GET_FANTASY_NAME           = 0x92;

	/** Cargar encabezamiento y pie de documentos. (0x5D) */
	public int CMD_SET_HEADER_TRAILER         = 0x5D;
	/** Consultar encabezamiento y pie de documentos. (0x5E) */
	public int CMD_GET_HEADER_TRAILER         = 0x5E;

	/** Cargar logotipo del cliente. (0x90) */
	public int CMD_STORE_LOGO_DATA            = 0x90;
	/** Borrar logotipo del cliente. (0x91) */
	public int CMD_RESET_LOGO_DATA            = 0x91;


	// Comandos de control fiscal

	/** Consultar capacidad restante de registros diarios. (0x37) */
	public int CMD_GET_HISTORY_CAPACITY       = 0x37;
	/** Cerrar jornada fiscal imprimiendo reporte. (0x39) */
	public int CMD_DAILY_CLOSE                = 0x39;
	/** Imprimir reporte de auditoría por fecha. (0x3A) */
	public int CMD_DAILY_CLOSE_BY_DATE        = 0x3A;
	/** Imprimir reporte de auditoría por número de Z. (0x3B) */
	public int CMD_DAILY_CLOSE_BY_NUMBER      = 0x3B;
	/** Consultar registro diario. (0x3C) */
	public int CMD_GET_DAILY_REPORT           = 0x3C;
	/** Consultar memoria de trabajo. (0x67) */
	public int CMD_GET_WORKING_MEMORY         = 0x67;
	/** Iniciar consulta de la información de IVA. (0x70) */
	public int CMD_GET_FIRST_IVA_DATA         = 0x70;
	/** Continuar consulta de la información de IVA. (0x71) */
	public int CMD_GET_NEXT_IVA_DATA          = 0x71;


	// Comandos comunes a varios tipos de documentos

	/** Cancelar documento. (0x98) */
	public int CMD_CANCEL_DOCUMENT            = 0x98;
	/** Reimprimir el último documento emitido. (0x99) */
	public int CMD_REPRINT_DOCUMENT           = 0x99;
	/** Cargar código de barras. (0x5A) */
	public int CMD_SET_BAR_CODE               = 0x5A;
	/** Cargar datos del cliente. (0x62) */
	public int CMD_SET_CUSTOMER_DATA          = 0x62;
	/** Cargar información de remito o de comprobante original. (0x93) */
	public int CMD_SET_EMBARK_NUMBER          = 0x93;
	/** Consultar información de remito o de comprobante original. (0x94) */
	public int CMD_GET_EMBARK_NUMBER          = 0x94;
	/** Avanzar papel de ticket. (0x50) */
	public int CMD_FEED_TICKET                = 0x50;
	/** Avanzar papel de auditoría. (0x51) */
	public int CMD_FEED_JOURNAL               = 0x51;
	/** Avanzar papeles de ticket y auditoría. (0x52) */
	public int CMD_FEED_TICKET_AND_JOURNAL    = 0x52;


	// Comandos de documentos fiscales

	/** DF: Abrir documento fiscal. (0x40) */
	public int CMD_OPEN_FD                    = 0x40;
	/** DF: Imprimir texto fiscal. (0x41) */
	public int CMD_PRINT_FISCAL_TEXT          = 0x41;
	/** DF: Imprimir ítem. (0x42) */
	public int CMD_PRINT_LINE_ITEM            = 0x42;
	/** DF: Descuento o recargo sobre el último ítem. (0x55) */
	public int CMD_LAST_ITEM_DISCOUNT         = 0x55;
	/** DF: Descuento general. (0x54) */
	public int CMD_GENERAL_DISCOUNT           = 0x54;
	/** DF: Devolución de envases, bonificaciones y recargos. (0x6D) */
	public int CMD_RETURN_RECHARGE            = 0x6D;
	/** DF: Recargo de IVA a responsable no inscripto. (0x61) */
	public int CMD_CHARGE_NON_REGISTERED_TAX  = 0x61;
	/** DF: Percepciones sobre el IVA. (0x60) */
	public int CMD_PERCEPTIONS                = 0x60;
	/** DF: Consultar subtotal. (0x43) */
	public int CMD_SUBTOTAL                   = 0x43;
	/** DF: Definir líneas de texto en recibos. (0x97) */
	public int CMD_RECEIPT_TEXT               = 0x97;
	/** DF: Imprimir total y pago. (0x44) */
	public int CMD_TOTAL_TENDER               = 0x44;
	/** DF: Cerrar documento fiscal. (0x45) */
	public int CMD_CLOSE_FD                   = 0x45;


	// Comandos de documentos no fiscales

	/** DNF: Abrir documento no fiscal en impresora Ticket. (0x48) */
	public int CMD_OPEN_NFD_TICKET            = 0x48;
	/** DNF: Abrir documento no fiscal en impresora Slip. (0x47) */
	public int CMD_OPEN_NFD_SLIP              = 0x47;
	/** DNF: Imprimir texto no fiscal. (0x49) */
	public int CMD_PRINT_NON_FISCAL_TEXT      = 0x49;
	/** DNF: Cerrar documento no fiscal. (0x4A) */
	public int CMD_CLOSE_NFD                  = 0x4A;
	/** DNF: Cortar documento no fiscal. (0x4B) */
	public int CMD_CUT_NFD                    = 0x4B;


	// Comandos de documentos no fiscales homologados

	/** DNFH: Abrir documento no fiscal homologado. (0x80) */
	public int CMD_OPEN_NFHD                  = 0x80;
	/** DNFH: Imprimir ítem en remito u orden de salida. (0x82) */
	public int CMD_PRINT_EMBARK_ITEM          = 0x82;
	/** DNFH: Imprimir ítem en resumen de cuenta o en cargo a la habitación. (0x83) */
	public int CMD_PRINT_ACCOUNT_ITEM         = 0x83;
	/** DNFH: Imprimir ítem en cotización. (0x84) */
	public int CMD_PRINT_QUOTATION_ITEM       = 0x84;
	/** DNFH: Cerrar documento no fiscal homologado. (0x81) */
	public int CMD_CLOSE_NFHD                 = 0x81;
	/** Imprimir documento no fiscal homologado para farmacias. (0x68) */
	public int CMD_PRINT_PHARMACY_NFHD        = 0x68;
	/** Imprimir documento no fiscal homologado para reparto. (0x69) */
	public int CMD_PRINT_DELIVERY_NFHD        = 0x69;


	// Comandos de documentos voucher

	/** Voucher: Iniciar carga de datos del voucher. (0x6A) */
	public int CMD_SET_VOUCHER_DATA_1         = 0x6A;
	/** Voucher: Finalizar carga de datos del voucher. (0x6B) */
	public int CMD_SET_VOUCHER_DATA_2         = 0x6B;
	/** Voucher: Imprimir voucher. (0x6C) */
	public int CMD_PRINT_VOUCHER              = 0x6C;


	// Otros comandos

	/** Abrir cajón de dinero. (0x7B) */
	public int CMD_OPEN_DRAWER                = 0x7B;
	/** Escribir en visor. (0xB2) */
	public int CMD_WRITE_DISPLAY              = 0xB2;


	//// ESTADO DE IMPRESORA /////////////////

	///** Impresora ocupada. (Bit 0 del estado de impresora) */
	//public int PST_PRINTER_BUSY               = 0x0001;

	//public int PST_BIT_1                      = 0x0002;

	/** Error de impresora. (Bit 2 del estado de impresora) */
	// También puede señalar impresora ocupada en impresoras para Chile.
	public int PST_PRINTER_ERROR              = 0x0004;

	/** Impresora fuera de línea. No ha podido comunicarse con la impresora dentro del período de tiempo establecido. (Bit 3 del estado de impresora) */
	public int PST_PRINTER_OFFLINE            = 0x0008;

	/** Falta papel del diario. (Bit 4 del estado de impresora) */
	public int PST_JOURNAL_PAPER_OUT          = 0x0010;

	/** Falta papel de tickets. (Bit 5 del estado de impresora) */
	public int PST_TICKET_PAPER_OUT           = 0x0020;

	/** Buffer de impresión lleno. Cualquier comando que se envíe cuando este bit está en 1 no se ejecuta y debe ser reenviado por la aplicación. (Bit 6 del estado de impresora) */
	public int PST_PRINT_BUFFER_FULL          = 0x0040;

	/** Buffer de impresión vacío. Todos los comandos fueron enviados a la impresora. (Bit 7 del estado de impresora) */
	public int PST_PRINT_BUFFER_EMPTY         = 0x0080;

	/** Tapa de impresora abierta. (Bit 8 del estado de impresora) */
	public int PST_PRINTER_COVER_OPEN         = 0x0100;

	//public int PST_BIT_9                      = 0x0200;

	//public int PST_BIT_10                     = 0x0400;

	//public int PST_BIT_11                     = 0x0800;

	//public int PST_BIT_12                     = 0x1000;

	//public int PST_BIT_13                     = 0x2000;

	/** Cajón de dinero cerrado o ausente. (Bit 14 del estado de impresora) */
	public int PST_MONEY_DRAWER_CLOSED        = 0x4000;

	/** Suma lógica (OR) de los bits 2 a 5, 8 y 14. Este bit se encuentra en 1 siempre que alguno de los bits mencionados se encuentre en 1. (Bit 15 del estado de impresora) */
	public int PST_BITWISE_OR                 = 0x8000;


	//// ESTADO FISCAL ///////////////////////

	/** Error en chequeo de memoria fiscal. Al encenderse la impresora se produjo un error en el checksum. La impresora no funcionará. (Bit 0 de estado fiscal) */
	public int FST_FISCAL_MEMORY_CRC_ERROR    = 0x0001;

	/** Error en chequeo de memoria de trabajo. Al encenderse la impresora se produjo un error en el checksum. La impresora no funcionará. (Bit 1 de estado fiscal) */
	public int FST_WORKING_MEMORY_CRC_ERROR   = 0x0002;

	//public int FST_BIT_2                      = 0x0004;

	/** Comando desconocido. El comando recibido no fue reconocido. (Bit 3 de estado fiscal) */
	public int FST_UNKNOWN_COMMAND            = 0x0008;

	/** Datos inválidos en un campo. Uno de los campos del comando recibido tiene datos no válidos (por ejemplo, datos no numéricos en un campo numérico). (Bit 4 de estado fiscal) */
	public int FST_INVALID_DATA_FIELD         = 0x0010;

	/** Comando inválido para el estado fiscal actual. Se ha recibido un comando que no es válido en el estado actual del controlador (por ejemplo, abrir un documento no fiscal cuando se encuentra abierto un documento fiscal). (Bit 5 de estado fiscal) */
	public int FST_INVALID_COMMAND            = 0x0020;

	/** Desborde de acumulador. El acumulador de una transacción, del total diario o del IVA se desbordará a raíz de un comando recibido. El comando no es ejecutado. Este bit debe ser monitoreado por la aplicación para emitir el correspondiente aviso. (Bit 6 de estado fiscal) */
	public int FST_ACCUMULATOR_OVERFLOW       = 0x0040;

	/** Memoria fiscal llena, bloqueada o dada de baja. No se permite abrir un comprobante fiscal. (Bit 7 de estado fiscal) */
	public int FST_FISCAL_MEMORY_FULL         = 0x0080;

	/** Memoria fiscal a punto de llenarse. La memoria fiscal tiene 30 o menos registros libres. Este bit debe ser monitoreado por la aplicación para emitir el correspondiente aviso. (Bit 8 de estado fiscal) */
	public int FST_FISCAL_MEMORY_ALMOST_FULL  = 0x0100;

	/** Terminal fiscal certificada. Indica que la impresora ha sido inicializada. (Bit 9 de estado fiscal) */
	public int FST_DEVICE_CERTIFIED           = 0x0200;

	/** Terminal fiscal fiscalizada. Indica que la impresora ha sido inicializada. (Bit 10 de estado fiscal) */
	public int FST_DEVICE_FISCALIZED          = 0x0400;

	/** Error en ingreso de fecha. Se ha ingresado una fecha inválida. Para volver el bit a 0 debe ingresarse una fecha válida. (Bit 11 de estado fiscal) */
	public int FST_CLOCK_ERROR                = 0x0800;

	/** Documento fiscal abierto. Este bit se encuentra en 1 siempre que un documento fiscal (factura, recibo oficial o nota de crédito) se encuentra abierto. (Bit 12 de estado fiscal) */
	public int FST_FISCAL_DOCUMENT_OPEN       = 0x1000;

	/** Documento abierto (solo impresoras que soportan STATPRN). Este bit se encuentra en 1 siempre que un documento (fiscal, no fiscal o no fiscal homologado) se encuentra abierto. (Bit 13 de estado fiscal) */
	public int FST_DOCUMENT_OPEN              = 0x2000;

	/** Estado intermedio (STATPRN) activo (solo impresoras que soportan STATPRN). Este bit se encuentra en 1 cuando se intenta enviar un comando estando activado el STATPRN. El comando es rechazado. (Bit 14 de estado fiscal) */
	public int FST_STATPRN_ACTIVE             = 0x4000;

	/** Documento abierto en impresora Ticket (solo impresoras que no soportan STATPRN). (Bit 13 de estado fiscal) */
	public int FST_DOCUMENT_OPEN_TICKET       = 0x2000;

	/** Documento abierto en impresora Slip (solo impresoras que no soportan STATPRN). (Bit 14 de estado fiscal) */
	public int FST_DOCUMENT_OPEN_SLIP         = 0x4000;

	/** Suma lógica (OR) de los bits 0 a 8. Este bit se encuentra en 1 siempre que alguno de los bits mencionados se encuentre en 1. (Bit 15 de estado fiscal) */
	public int FST_BITWISE_OR                 = 0x8000;
}
