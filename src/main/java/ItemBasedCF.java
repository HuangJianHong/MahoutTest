import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 基本的步骤（过程）：
 * 1、 分析各个用户对物品（item）评分
 * 2、计算出所有物品之间的相识度（余弦相似度、欧式距离）
 * 3、找到与当前用户购买的物品，相似度比较高的其他物品
 * 4、把这些物品推荐给当前用户
 */
public class ItemBasedCF {

    public static void main(String[] args) throws IOException, TasteException {
        //根据数据源建立数据模型，就是： 打分矩阵
        String file = "D:\\temp\\data\\ratingdata.txt";
        DataModel model = new FileDataModel(new File(file));

        //计算物品的相似度矩阵
        ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);

        //建立基于物品的推荐引擎
        Recommender recommender = new GenericItemBasedRecommender(model, similarity);

        //推荐商品：为每个用户推荐商品，最多每个用户推荐3个商品
        LongPrimitiveIterator userIDs = model.getUserIDs();
        while (userIDs.hasNext()) {
            //取出一个用户ID
            long userID = userIDs.nextLong();

            //输出
            System.out.println("用户ID： " + userID);

            //为该用户进行基于物品的推荐
            List<RecommendedItem> items = recommender.recommend(userID, 3);
            for (RecommendedItem item : items) {
                System.out.println("物品的ID: " + item.getItemID() + "\t 评分：" + item.getValue());
            }
            System.out.println("*****************************************");

        }
    }

}
