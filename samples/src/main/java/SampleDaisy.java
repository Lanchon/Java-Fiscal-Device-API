/*
 * Copyright (C) 2015 EDA Ltd.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 *
 * @author nikolabintev@edabg.com
 */
import com.taliter.fiscal.device.FiscalDeviceIOException;
import com.taliter.fiscal.device.daisy.DaisyFiscalPrinter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple example that prints the diagnostic information of Daisy fiscal device. 
 */
public class SampleDaisy {
    
    public static void main(String[] args) throws Exception {
        DaisyFiscalPrinter printer = null;
        try {
            printer = new DaisyFiscalPrinter("COM1");

            printer.cmdPrintDiagnosticInfo();
        } catch (FiscalDeviceIOException ex) {
            Logger.getLogger(SampleDaisy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SampleDaisy.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            printer.close();
        }
    }
}