package cp.servlet.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cp.dao.DataBase;
import cp.json.JoinSchool;

/**
 * Servlet implementation class QRcodeMake
 */
@WebServlet("/QRcodeMake")
public class QRcodeMake extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QRcodeMake() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id = request.getParameter("id");
		Gson gson = new Gson();
		String JoinSchools = DataBase.selectWithLabel("SELECT * FROM join_school WHERE act_id = ? AND st_code <> 0 ORDER BY sch_id,year", new String[]{String.valueOf(id)});
		List<JoinSchool> jschool_lst = gson.fromJson(JoinSchools,new TypeToken<List<JoinSchool>>(){}.getType());
		
		//make qrcode
		Rectangle PrintPageSize = PageSize.A4;
		Rectangle BarcodeSize = new Rectangle(PageSize.A4.getWidth() / 8, PageSize.A4.getHeight() / 14);
		Document document = new Document(PrintPageSize); 
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter writer = null;
		try {
			BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			Font fontRedCN = new Font(bfChinese, 12, Font.NORMAL, new BaseColor(255, 0, 0));
			Font fontBlueCN = new Font(bfChinese, 12, Font.NORMAL, new BaseColor(0, 0, 255));
			writer = PdfWriter.getInstance( document, buffer );
			document.open(); 
			PdfContentByte cb = writer.getDirectContent(); 
			//建立PdfPTable物件並設定其欄位數
			int columnSize = (int)(PrintPageSize.getWidth() / BarcodeSize.getWidth()) - 1;
	        PdfPTable table = new PdfPTable(1); 
	        //設定table的寬度
	        table.setWidthPercentage(100f);
	        //設定table的title
	        PdfPCell title = new PdfPCell();
	        //合併儲存格
	        title.setColspan(1);
	        title.addElement(new Phrase("Table's Title"));
	        table.addCell(title);
	        table.setHorizontalAlignment(Element.ALIGN_CENTER);
	        
	        int last_barNum = 0;
	        for(JoinSchool school : jschool_lst) {
	        	int st_code = Integer.valueOf(school.getStCode());
	        	int ed_code = Integer.valueOf(school.getEdCode());
	        	
	        	for(int i = st_code;i <= ed_code;i++) {
				    String formatStr = "%03d";
				    String formatAns = String.format(formatStr, i);   // 數字補0
				    BarcodeQRCode qrcode = new BarcodeQRCode(school.getYear() + formatAns, 1, 1, null);
				    Image barCodeImage = null;
				    if(i%2 != 0)
				    	barCodeImage = Image.getInstance(qrcode.createAwtImage(Color.BLUE, Color.WHITE), null);
				    else
				    	barCodeImage = Image.getInstance(qrcode.createAwtImage(Color.RED, Color.WHITE), null);
				    barCodeImage.scaleToFit(BarcodeSize);
				    PdfPCell QRcell = new PdfPCell(barCodeImage);
				    QRcell.setMinimumHeight(71);
				    QRcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				    QRcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				    
				    Chunk chunk = null;
				    chunk = new Chunk(school.getYear() + formatAns, (i%2 != 0)?fontBlueCN:fontRedCN);
				    Phrase phrase = new Phrase(chunk);
				    PdfPCell textcell = new PdfPCell(phrase);
				    textcell.setMinimumHeight(14);
				    textcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				    textcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table.addCell(QRcell);
			        table.addCell(textcell);
			        last_barNum++;
				}
			}
			last_barNum = last_barNum % columnSize;
			for (int i=0;i<columnSize - last_barNum;i++)
				table.addCell(new PdfPCell());
	        document.add(table);
			document.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		byte[] bytes = buffer.toByteArray(); 
		if(bytes!=null)
		{
			response.setContentType("application/pdf"); 
			//PrintWriter out = response.getWriter();
			DataOutput output = new DataOutputStream( response.getOutputStream() ); 
			response.setContentLength(bytes.length); 

			for( int i = 0; i < bytes.length; i++ ) 
			{ 
				output.writeByte( bytes[i] ); 
			} 
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
