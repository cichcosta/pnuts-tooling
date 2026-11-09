package PT.Minimal.util;

import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import pt.gov.dgci._2007.ies_da.IESDA;
import pt.gov.dgci._2007.ies_da.RostoType;
import pt.gov.dgci._2007.ies_da.RostoType.Q10NifEmpresaOrigemLista;
import pt.gov.dgci._2007.ies_da.RostoType.Q10NifEmpresaOrigemLista.Q10NifEmpresaOrigemLinha;


























public class Mies
{
  public Mies()
    throws JAXBException
  {
    IESDA wies = new IESDA();
    
    RostoType wrosto = new RostoType();
    wrosto.setQ101(Boolean.TRUE);
    

    RostoType.Q10NifEmpresaOrigemLista wq10lista = new RostoType.Q10NifEmpresaOrigemLista();
    RostoType.Q10NifEmpresaOrigemLista.Q10NifEmpresaOrigemLinha wq10listalinha = new RostoType.Q10NifEmpresaOrigemLista.Q10NifEmpresaOrigemLinha();
    
    wq10listalinha.setQ10NifEmpresaOrigem(123456789);
    wq10lista.getQ10NifEmpresaOrigemLinha().add(wq10listalinha);
    wq10listalinha.setQ10NifEmpresaOrigem(987654321);
    wq10lista.getQ10NifEmpresaOrigemLinha().add(wq10listalinha);
    wrosto.setQ10NifEmpresaOrigemLista(wq10lista);
    
    wies.setRosto(wrosto);
    JAXBContext jc = JAXBContext.newInstance("pt.gov.dgci._2007.ies_da");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty("jaxb.encoding", "UTF-8");
    marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
    marshaller.marshal(wies, System.out);
  }
}
