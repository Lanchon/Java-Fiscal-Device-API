package com.taliter.fiscal.device.icl;

import com.taliter.fiscal.device.hasar.HasarFiscalDeviceSource;
import com.taliter.fiscal.port.FiscalPortSource;

/**
 * A ICLFiscalDevice factory
 * @author nikolabintev@edabg.com
 */
public class ICLFiscalDeviceSource extends HasarFiscalDeviceSource{
    
    /**
     * Create an instance of ICLFiscalDeviceSource.
     */
    public ICLFiscalDeviceSource() {
        super();
        
        setTimeout(3000);
        setMaxTries(3);
        setEncoding("Windows-1251");
        setExtendedProtocol(false);
        setHandleExtendedProtocol(false);
    }

    /**
     * Create an instance of ICLFiscalDeviceSource.
     * @param portSource The ICL fiscal device's port.
     */
    public ICLFiscalDeviceSource(FiscalPortSource portSource) { 
        super(portSource);
        
        setTimeout(3000);
        setMaxTries(3);
        setEncoding("Windows-1251");
        setExtendedProtocol(false);
        setHandleExtendedProtocol(false);
    }
    
    
    /**
     * Get ICL fiscal device
     * @return Returns ICL Fiscal Device object
     * @throws Exception 
     */
    @Override
    public ICLFiscalDevice getFiscalDevice() throws Exception {
        return new ICLFiscalDevice(getPortSource() != null ? getPortSource().getFiscalPort() : null, getTimeout(), getMaxTries(), getEncoding());
        
    }
}
