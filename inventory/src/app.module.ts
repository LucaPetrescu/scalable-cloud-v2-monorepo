import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DatabaseService } from './api/database/database.service';
import { MongooseModule } from '@nestjs/mongoose';
import { ProductModule } from './api/product/product.module';

@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27018/products'),
    ProductModule,
  ],
  controllers: [AppController],
  providers: [AppService, DatabaseService],
})
export class AppModule {}
